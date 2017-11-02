Create a reader layer which 

Iterable<DataEnvelope> read(String select, String where, String orderBy)

Internally, this:

delegates to a Repository via a factory which works out whether to use a StreamingSupplier or a FixedSupplier (i.e. from internal storage)

So, we'll need two Repository impls; Streaming (which reads from some soure like another REST API or a simulator), PointInTime (which reads from an internal store like Mongo)


** DataEnvelope
- soe kind of source identifier, start with a string
- a payload of type Map<Stroig, Object>


Using the Running Service
----

*Examples*
    
    
Addressing the sibling streaming-producer service on localhost:2100 (dataset name = Local HTTP Producer)

    http://localhost:8080/dataset/Local%20HTTP%20Producer/sample
    http://localhost:8080/dataset/Local%20HTTP%20Producer/content?select=name,widget.price
    http://localhost:8080/dataset/Local%20HTTP%20Producer/content?where=widget.item%3E50
    http://localhost:8080/dataset/Local%20HTTP%20Producer/content?where=widget.item between 495 and 498 and name like '%25496%25'
    
Addressing the data in antlr.sample5 on MongoDb at localhost:27017 (dataset name = MongoDB Producer - Size 10)

    http://localhost:8080/dataset/MongoDB%20Producer%20-%20Size%205/sample
    http://localhost:8080/dataset/MongoDB%20Producer%20-%20Size%205/content?where=age%20%3C%2055&select=name,age&orderBy=age%20desc

Addressing the data in antlr.sample10 on MongoDb at localhost:27017 (dataset name = MongoDB Producer - Size 10)

    http://localhost:8080/dataset/MongoDB%20Producer%20-%20Size%2010/sample
    http://localhost:8080/dataset/MongoDB%20Producer%20-%20Size%2010/content?where=age%20%3C%2055&select=name,age&orderBy=age%20desc
    
    
TODOs
----

You **must** come to terms with this:
    https://stackoverflow.com/questions/39474808/vertx-http-server-thread-has-been-blocked-for-xxxx-ms-time-limit-is-2000
    
Ensure that you are not blocking inside the handler impls to which the routers delegate!!        