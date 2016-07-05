(function() {
    'use strict';

    angular
        .module('timeManagerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('worker', {
            parent: 'entity',
            url: '/worker',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Workers'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/worker/workers.html',
                    controller: 'WorkerController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('worker-detail', {
            parent: 'entity',
            url: '/worker/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Worker'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/worker/worker-detail.html',
                    controller: 'WorkerDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Worker', function($stateParams, Worker) {
                    return Worker.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('worker.new', {
            parent: 'worker',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worker/worker-dialog.html',
                    controller: 'WorkerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                currentMonthHours: null,
                                totalHours: null,
                                totalExpenses: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('worker', null, { reload: true });
                }, function() {
                    $state.go('worker');
                });
            }]
        })
        .state('worker.edit', {
            parent: 'worker',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worker/worker-dialog.html',
                    controller: 'WorkerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Worker', function(Worker) {
                            return Worker.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('worker', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('worker.delete', {
            parent: 'worker',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/worker/worker-delete-dialog.html',
                    controller: 'WorkerDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Worker', function(Worker) {
                            return Worker.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('worker', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
