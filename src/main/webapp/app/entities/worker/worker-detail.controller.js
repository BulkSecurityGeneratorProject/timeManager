(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkerDetailController', WorkerDetailController);

    WorkerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Worker', 'WorkTime', 'Expense'];

    function WorkerDetailController($scope, $rootScope, $stateParams, entity, Worker, WorkTime, Expense) {
        var vm = this;

        vm.worker = entity;

        var unsubscribe = $rootScope.$on('timeManagerApp:workerUpdate', function(event, result) {
            vm.worker = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
