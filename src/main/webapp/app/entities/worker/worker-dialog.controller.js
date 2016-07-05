(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkerDialogController', WorkerDialogController);

    WorkerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Worker', 'WorkTime', 'Expense'];

    function WorkerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Worker, WorkTime, Expense) {
        var vm = this;

        vm.worker = entity;
        vm.clear = clear;
        vm.save = save;
        vm.worktimes = WorkTime.query();
        vm.expenses = Expense.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.worker.id !== null) {
                Worker.update(vm.worker, onSaveSuccess, onSaveError);
            } else {
                Worker.save(vm.worker, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timeManagerApp:workerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
