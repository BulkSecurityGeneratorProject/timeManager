(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('work-time', {
            parent: 'entity',
            url: '/work-time',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WorkTimes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/work-time/work-times.html',
                    controller: 'WorkTimeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('work-time-detail', {
            parent: 'entity',
            url: '/work-time/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WorkTime'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/work-time/work-time-detail.html',
                    controller: 'WorkTimeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'WorkTime', function($stateParams, WorkTime) {
                    return WorkTime.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('work-time.new', {
            parent: 'work-time',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work-time/work-time-dialog.html',
                    controller: 'WorkTimeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                description: null,
                                date: null,
                                hours: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('work-time', null, { reload: true });
                }, function() {
                    $state.go('work-time');
                });
            }]
        })
        .state('work-time.edit', {
            parent: 'work-time',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work-time/work-time-dialog.html',
                    controller: 'WorkTimeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['WorkTime', function(WorkTime) {
                            return WorkTime.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('work-time', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('work-time.delete', {
            parent: 'work-time',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/work-time/work-time-delete-dialog.html',
                    controller: 'WorkTimeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['WorkTime', function(WorkTime) {
                            return WorkTime.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('work-time', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
