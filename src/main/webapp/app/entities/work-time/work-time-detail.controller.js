(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkTimeDetailController', WorkTimeDetailController);

    WorkTimeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'WorkTime', 'Project', 'Worker'];

    function WorkTimeDetailController($scope, $rootScope, $stateParams, entity, WorkTime, Project, Worker) {
        var vm = this;

        vm.workTime = entity;

        var unsubscribe = $rootScope.$on('timeManagerApp:workTimeUpdate', function(event, result) {
            vm.workTime = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
