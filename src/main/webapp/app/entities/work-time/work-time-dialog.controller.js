(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkTimeDialogController', WorkTimeDialogController);

    WorkTimeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'WorkTime', 'Project', 'Worker'];

    function WorkTimeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, WorkTime, Project, Worker) {
        var vm = this;

        vm.workTime = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.projects = Project.query();
        vm.workers = Worker.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.workTime.id !== null) {
                WorkTime.update(vm.workTime, onSaveSuccess, onSaveError);
            } else {
                WorkTime.save(vm.workTime, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('timeManagerApp:workTimeUpdate', result);
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
