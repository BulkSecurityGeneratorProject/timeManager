(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkTimeDeleteController',WorkTimeDeleteController);

    WorkTimeDeleteController.$inject = ['$uibModalInstance', 'entity', 'WorkTime'];

    function WorkTimeDeleteController($uibModalInstance, entity, WorkTime) {
        var vm = this;

        vm.workTime = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            WorkTime.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
