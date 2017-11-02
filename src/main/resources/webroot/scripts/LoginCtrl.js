dragoman.controller('LoginCtrl', ['$scope', '$http', '$window', function($scope, $http, $window) {

    $scope.credentials = {
        username: '',
        password: ''
    };

	$scope.logout = function() {
		$http.post("/dragoman/logout")
		.success(function() {
		$scope.error = '';
            $scope.refreshPage();
		}).error(function(error){
			console.error(error);
			$scope.error = error;
		});
	};

	$scope.login = function() {
		$http.post("/dragoman/login", $scope.credentials)
		.success(function() {
		$scope.error = '';
            $scope.refreshPage();
		}).error(function(error){
			console.error(error);
			$scope.error = error;
			$scope.credentials.password = '';
		});
	};

	$scope.refreshPage = function() {
	    // This is a standard DOM method which you can access injecting the $window service.
        // If you want to be sure to reload the page from the server, pass true as a parameter to reload
        // Since that requires interaction with the server, it will be slower so do it only if necessary
	    $window.location.reload(true);
	}
}]);