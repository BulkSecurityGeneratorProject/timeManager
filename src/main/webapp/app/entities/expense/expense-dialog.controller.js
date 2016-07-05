(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('ExpenseDialogController', ExpenseDialogController);

    ExpenseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Expense', 'Worker'];

    function ExpenseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Expense, Worker) {
        var vm = this;

        vm.expense = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.workers = Worker.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.expense.id !== null) {
                Expense.update(vm.expense, onSaveSuccess, onSaveError);
            } else {
                Expense.save(vm.expense, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timeManagerApp:expenseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
