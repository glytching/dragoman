dragoman.controller('ManageDatasetsCtrl', ['$scope', '$http', '$location', function($scope, $http, $location) {

    $scope.sortType = 'name'; // default sort type
    $scope.sortReverse = false;  // default sort order
    $scope.searchTerm = '';     // default search/filter term
    $scope.subscribed = false;
    $scope.showStackTrace = false;

    var eventBus = undefined;

	var getAllDatasets = function() {
	    console.log('Getting all datasets')
		$http.get("/dragoman/datasets")
            .success(function(data) {
                $scope.datasets = data;
                $scope.datasetContents = undefined;
            })
            .error(function(error) {
                console.error('Failed to get all datasets, due to: ' + JSON.stringify(error));
                $scope.error = error;
            });
	};

	var discardError = function() {
	    $scope.error = undefined;
	};

	$scope.discardError = function() {
	    $scope.error = undefined;
	};

	$scope.addDataset = function() {
	    discardError();

		$scope.pendingDataset = {
			name: '',
			owner: '',
			source: '',
			subscriptionControlField: '',
			subscriptionControlFieldPattern: ''
		};
		$scope.datasets = undefined;
	};

	$scope.editDataset = function(dataset) {
	    discardError();

		$http.get("/dragoman/dataset/" + dataset.id + "")
            .success(function(data) {
                $scope.pendingDataset = data;
                $scope.datasets = undefined;
            })
            .error(function(error) {
                console.error('Failed to get dataset, due to: ' + JSON.stringify(error));
                $scope.error = error;
            });
	};

	$scope.cancelPending = function() {
		$scope.pendingDataset = undefined;
		getAllDatasets();
	};

	$scope.savePending = function() {
        $http.post("/dragoman/dataset", $scope.pendingDataset)
            .success(function(data) {
                $scope.pendingDataset = undefined;
                getAllDatasets();
            })
            .error(function(error) {
                console.error('Failed to create dataset, due to: ' + JSON.stringify(error));
                $scope.error = error;
            });
	};

	$scope.delete = function(dataset) {
	    discardError();

		$http.delete("/dragoman/dataset/" + dataset.id)
            .success(function() {
                getAllDatasets();
            })
            .error(function(error) {
                console.error('Failed to delete dataset, due to: ' + JSON.stringify(error));
                $scope.error = error;
            });
	};

	$scope.sampleContents = function(dataset) {
	    discardError();

	    $scope.criteria = {
    			select:'',
    			where:'',
    			orderBy:'',
    			dataset: dataset
    		};

        $scope.getDatasetSample();
	};

	$scope.getDatasetSample = function() {
        $http.get("/dragoman/dataset/" + $scope.criteria.dataset.id + "/sample")
            .success(function(data) {
                $scope.datasetContents = data;
                $scope.setLastSubscriptionReceipt();
            })
            .error(function(error) {
                console.error('Failed to get dataset sample, due to: ' + JSON.stringify(error));
                $scope.error = error;
            });
	};

	$scope.getContents = function() {
        $http.get("/dragoman/dataset/" + $scope.criteria.dataset.id + "/content" +
            "?select=" + $scope.criteria.select +
            "&where=" + $scope.criteria.where +
            "&orderBy=" + $scope.criteria.orderBy + "&subscribe=" + $scope.subscribed)
            .success(function(data) {
                $scope.datasetContents = data;

                $scope.setLastSubscriptionReceipt();
            })
            .error(function(error) {
                console.error('Failed to get dataset contents, due to: ' + JSON.stringify(error));
                $scope.error = error;

                if ($scope.subscribed) {
                    $scope.subscribed = false;
                }
            });
	};

	$scope.resetContents = function() {
	    // reset the criteria
	    $scope.criteria.select = '';
	    $scope.criteria.where = '';
	    $scope.criteria.orderBy = '';

	    // grab a fresh sample
	    $scope.getDatasetSample();
	};

	$scope.hideContents = function() {
		$scope.datasetContents = undefined;
	};

	$scope.subscribe = function() {
	    var currentDatasetId = $scope.criteria.dataset.id;
        if ($scope.subscribed) {
            console.log("Unsubscribing for: " + currentDatasetId);
            $http.delete("/dragoman/dataset/" + currentDatasetId + "/content")
                .success(function(data) {
                    console.log('Unsubscribed for: ' + currentDatasetId);
                })
                .error(function(error) {
                    console.error('Failed to unsubscribe from ' + currentDatasetId + ', due to: ' + JSON.stringify(error));
                    $scope.error = error;
                });
            $scope.subscribed = false;
            eventBus = undefined;
        } else {
            // prepare to receive the event stream
            connectToEventBus(currentDatasetId);

            $scope.subscribed = true;

            // grab the contents for the current criteria _as of_ now
            $scope.getContents();
        }
	};

	var connectToEventBus = function (datasetId) {
		eventBus = new vertx.EventBus("http://" + $location.host() + ":" + $location.port() + "/dragoman/eventbus");
		eventBus.onopen = function(){
            console.log("Subscribing for: " + datasetId);
			eventBus.registerHandler(datasetId, function(response) {
                if (response.eventType === 'STREAM_EVENT') {
                    $scope.datasetContents.push(response.payload);
                } else if (response.eventType === 'STREAM_FAILED_EVENT') {
                    console.log('Received a subscription failure: ' + response.failureMessage);
                } else if (response.eventType === 'STREAM_COMPLETED_EVENT') {
                    console.log('Subscription response completed');
                }

                $scope.setLastSubscriptionReceipt();

                // tell angular to update its view bindings
				$scope.$apply();
			});
		};
	};

	$scope.setLastSubscriptionReceipt = function() {
        $scope.lastSubscriptionReceipt = new Date().toUTCString();
	};

	// initialise the page
	getAllDatasets();
}]);