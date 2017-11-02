dragoman.controller('HeaderCtrl', ['$scope', '$location', function($scope, $location) {

    $scope.isActive = function (viewLocation) {
        return $location.absUrl().endsWith(viewLocation);
    };

}]);