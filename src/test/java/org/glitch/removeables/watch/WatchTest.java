package org.glitch.removeables.watch;

import org.apache.commons.text.RandomStringGenerator;
import org.glitch.dragoman.ql.listener.groovy.Filter;
import org.glitch.dragoman.ql.listener.groovy.Mapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import rx.Observable;
import rx.subjects.ReplaySubject;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class WatchTest {

    @Mock
    private DynamicInspectorFactory dynamicInspectorFactory;
    @Mock
    private Filter filter;
    @Mock
    private Mapper mapper;

    private WatchFactory watchFactory;
    private final String source = "bingo";
    private RandomStringGenerator randomStrings;
    private StubPublisher publisher;

    @Before
    public void prepare() {
        randomStrings = new RandomStringGenerator.Builder().build();

        when(dynamicInspectorFactory.createFilter(anyString())).thenReturn(filter);
        when(filter.filter(any(Object.class))).thenReturn(true);

        when(dynamicInspectorFactory.createProjector(anyString())).thenReturn(mapper);
        when(mapper.map(any(Object.class))).thenAnswer(
                invocation -> invocation.getArguments()[0]

        );

        publisher = new StubPublisher();
        watchFactory = new WatchFactory(dynamicInspectorFactory, publisher);
    }

    @Test
    public void wtf() throws InterruptedException {
        ObservableList<EventWrapper> olist = new ObservableList<>(anyEventWrapper(), anyEventWrapper("ignore me"));

        Watch watch = watchFactory.create(olist.getObservable(), "map", "where", source);
        watch.start();

        olist.add(anyEventWrapper("ignore me"));
        olist.add(anyEventWrapper(source));
        olist.add(anyEventWrapper(source));
        olist.add(anyEventWrapper(source));
        olist.add(anyEventWrapper(source));
        olist.add(anyEventWrapper(source));
        // no need to slepp when the watch runs on the main thread (i.e. Schedulers.immediate())
        //        Thread.sleep(1500);
        watch.stop();

        // verify that everything in the list was played out!
        List<EventWrapper> expecteds = olist.getAll();

        for (EventWrapper expected : expecteds) {
            if (expected.getSource().equals(source)) {
                assertThat(publisher.contains(expected), is(true));
            } else {
                assertThat(publisher.contains(expected), is(false));
            }
        }

    }

    public static class ObservableList<T> {

        protected final List<T> list;
        protected final ReplaySubject<T> onAdd;

        @SafeVarargs
        public ObservableList(T... items) {
            this.list = new ArrayList<>();
            // buffer anything provided on construction
            this.onAdd = ReplaySubject.create();
            List<T> c = Arrays.asList(items);
            for (T t : c) {
                add(t);
            }
        }

        public void add(T value) {
            list.add(value);
            onAdd.onNext(value);
        }

        public Observable<T> getObservable() {
            return onAdd;
        }

        public List<T> getAll() {
            return list;
        }
    }

    private EventWrapper anyEventWrapper() {
        return anyEventWrapper(source);
    }

    private EventWrapper anyEventWrapper(String source) {
        return new EventWrapper(source, anyMap());
    }

    private Map<String, Object> anyMap() {
        return Stream.of(new SimpleEntry<>("name", aString()),
                new SimpleEntry<>("label", aString()))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    private String aString() {
        return randomStrings.generate(5);
    }
}
