(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Project', 'WorkTime'];

    function ProjectDetailController($scope, $rootScope, $stateParams, entity, Project, WorkTime) {
        var vm = this;

        vm.project = entity;

        var unsubscribe = $rootScope.$on('timeManagerApp:projectUpdate', function(event, result) {
            vm.project = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
