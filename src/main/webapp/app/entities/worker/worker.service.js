(function() {
    'use strict';
    angular
        .module('timeManagerApp')
        .factory('Worker', Worker);

    Worker.$inject = ['$resource'];

    function Worker ($resource) {
        var resourceUrl =  'api/workers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
