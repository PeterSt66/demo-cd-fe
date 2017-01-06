(function(angular) {
  var AppController = function($scope, Info, Admin) {
    $scope.refresh = function() {
        console.log('refresh()');
        Info.get(function(response) {
          $scope.info = response ? response : {};
          $scope.backendHostname = $scope.info.fe.infoBackend.host + ':' + $scope.info.fe.infoBackend.port;
        });
    }
    $scope.refresh();

    $scope.changeBackend = function() {
        hostName = $scope.backendHostname;
        console.log('changeBackend('+hostName+')');
        Admin.changeBackend(hostName, function() {
            console.log('Back from change ');
            $scope.refresh();
        })
    }

  }  
 
  AppController.$inject = ['$scope', 'Info', 'Admin'];
  angular.module("myApp.controllers").controller("AppController", AppController);

}(angular));