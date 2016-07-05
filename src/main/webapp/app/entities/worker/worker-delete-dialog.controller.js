(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkerDeleteController',WorkerDeleteController);

    WorkerDeleteController.$inject = ['$uibModalInstance', 'entity', 'Worker'];

    function WorkerDeleteController($uibModalInstance, entity, Worker) {
        var vm = this;

        vm.worker = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Worker.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
