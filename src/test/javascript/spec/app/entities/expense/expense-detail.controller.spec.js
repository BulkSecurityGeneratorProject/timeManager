'use strict';

describe('Controller Tests', function() {

    describe('Expense Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockExpense, MockWorker;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockExpense = jasmine.createSpy('MockExpense');
            MockWorker = jasmine.createSpy('MockWorker');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Expense': MockExpense,
                'Worker': MockWorker
            };
            createController = function() {
                $injector.get('$controller')("ExpenseDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'timeManagerApp:expenseUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
