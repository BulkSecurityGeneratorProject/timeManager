(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('ExpenseDetailController', ExpenseDetailController);

    ExpenseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Expense', 'Worker'];

    function ExpenseDetailController($scope, $rootScope, $stateParams, entity, Expense, Worker) {
        var vm = this;

        vm.expense = entity;

        var unsubscribe = $rootScope.$on('timeManagerApp:expenseUpdate', function(event, result) {
            vm.expense = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
