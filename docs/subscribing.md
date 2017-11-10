Subscriptions
======

Dragoman allows the user to register for updates to datasets by clicking on the `eye` icon next to the dataset name in the dataset contents view. 

#### Flow

The subscription is achieved by:

* `CLIENT` submits a `subscribe`call to the server, specifying:
  * The dataset id: this is the subscription key (so, yes, only one concurrent subscription is allowed for each dataset)
  * (Optionally) projections and predicates
* `SERVER` registers a periodic task which:
  * Runs the given query against this dataset
  * Applies a predicate derived from the dataset's configured `Subscription Control Field` to limit the results to those records which have been edited/created since the previous subscription period
  * Pushes the resolved records back to the client via Vert.x's EventBus
* `CLIENT` subscribes to events on the EventBus, using the dataset id as the subscription key and, on receipt of a subscription event, adds its payload to the colletion of records displayed in the dataset contents table.

#### Subscription Event Stream

The event stream emitted by the server contains events of the following type:

* `SubscriptionStreamEvent`: contains the payload for a single record 
* `SubscriptionStreamCompletedEvent`: indicates that the event stream for a given subscription period has completed successfully. This means that there will be no further events received until the next subscription period.
* `SubscriptionStreamFailedEvent`: indicates that the event stream for a given subscription period has terminated with a failure. This does not terminate the subscription but it does mean that there will be no further events received until the next subscription period.
 
#### Gotchas

* If you have read the above closely then you've probably already spotted that a subscription is implemented by polling the underlying dataset rather than by consuming an event stream emitted by the provider of the underlying dataset. There's an [open issue](https://github.com/glytching/dragoman/issues/1) to implement a proper event stream for the MongoDB hosted datasets. Sourcing an event stream from the HTTP datasets is a different matter; this depends on whether the underlying provider actually provides any such feature and it complicates the configuration of a dataset. An implementation would likely involve using polling as the default subscription mode with some additional dataset configuration allowing an event stream integration where the underlying provider supports it. This will probably emerge as an issue at some point.
* The fact that subscriptions are currently polling-backed does allow an ordering to be applied but a caller defined ordering conceptually has no place in a subscription since events _should_ be emitted as they are received so the subscription flow ignores `Order By`.
* When subscribing to HTTP sources you _may_ encounter clock sync issues. For HTTP sources all predicates (including the 'has changed since last subscription period' predicate) are applied within Dragoman.Since the 'has changed since last subscription period' predicate is time based any mismatch between the clock on the client side (i.e. Dragoman's clock) and the clock on the server side (i.e. the clock which resulted in the population of the `Subscription Control Field` value in the dataset records) could result in odd behaviour i.e. fewer results than expected being returned. If this happens then it's worth syncing  the clock on your Dragoman host against a NTP.
* The subscription view in the web application just displays whatever it gets, it does not attempt to merge updates with their existing entry in the view. Both insert and update events are displayed _as received_. Instead, all events from the subscription stream are appended to the top of the dataset view.