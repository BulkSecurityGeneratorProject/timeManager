(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('WorkTimeController', WorkTimeController);

    WorkTimeController.$inject = ['$scope', '$state', 'WorkTime'];

    function WorkTimeController ($scope, $state, WorkTime) {
        var vm = this;
        
        vm.workTimes = [];

        loadAll();

        function loadAll() {
            WorkTime.query(function(result) {
                vm.workTimes = result;
            });
        }
    }
})();
