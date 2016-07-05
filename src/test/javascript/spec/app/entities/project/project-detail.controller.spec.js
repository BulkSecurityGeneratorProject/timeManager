'use strict';

describe('Controller Tests', function() {

    describe('Project Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProject, MockWorkTime;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProject = jasmine.createSpy('MockProject');
            MockWorkTime = jasmine.createSpy('MockWorkTime');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Project': MockProject,
                'WorkTime': MockWorkTime
            };
            createController = function() {
                $injector.get('$controller')("ProjectDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'timeManagerApp:projectUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
