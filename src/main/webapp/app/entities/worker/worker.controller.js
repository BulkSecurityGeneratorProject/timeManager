(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkerController', WorkerController);

    WorkerController.$inject = ['$scope', '$state', 'Worker'];

    function WorkerController ($scope, $state, Worker) {
        var vm = this;
        
        vm.workers = [];

        loadAll();

        function loadAll() {
            Worker.query(function(result) {
                vm.workers = result;
            });
        }
    }
})();
