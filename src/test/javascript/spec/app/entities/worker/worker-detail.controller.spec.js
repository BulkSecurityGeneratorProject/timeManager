'use strict';

describe('Controller Tests', function() {

    describe('Worker Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockWorker, MockWorkTime, MockExpense;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockWorker = jasmine.createSpy('MockWorker');
            MockWorkTime = jasmine.createSpy('MockWorkTime');
            MockExpense = jasmine.createSpy('MockExpense');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Worker': MockWorker,
                'WorkTime': MockWorkTime,
                'Expense': MockExpense
            };
            createController = function() {
                $injector.get('$controller')("WorkerDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'timeManagerApp:workerUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
