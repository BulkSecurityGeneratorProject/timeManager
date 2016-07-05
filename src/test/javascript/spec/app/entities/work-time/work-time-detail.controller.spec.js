'use strict';

describe('Controller Tests', function() {

    describe('WorkTime Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockWorkTime, MockProject, MockWorker;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockWorkTime = jasmine.createSpy('MockWorkTime');
            MockProject = jasmine.createSpy('MockProject');
            MockWorker = jasmine.createSpy('MockWorker');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'WorkTime': MockWorkTime,
                'Project': MockProject,
                'Worker': MockWorker
            };
            createController = function() {
                $injector.get('$controller')("WorkTimeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'timeManagerApp:workTimeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
