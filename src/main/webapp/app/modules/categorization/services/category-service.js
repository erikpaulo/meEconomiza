define([ './module'
    , 'modules/categorization/services/category-constants'
    , 'modules/categorization/services/subcategory-resources'
    , 'modules/categorization/services/category-resources' ],
    function(module) {
        module.factory('CategoryService', ['$rootScope', '$q', '$mdDialog', 'CategoryConstants', 'CategoryResource', 'SubCategoryResource',
            function($rootScope, $q, $mdDialog, CategoryConstants, Category, SubCategory) {
                return {
                    newSubcategoryShortcut: function($scope, fullName){
                        var promise = $q(function(resolve, reject){
                            var category = {id: null};
                            category.subcategory = {}

                            if ( !fullName.match(/.* :: .* : .*/g) ){
                                addError($rootScope, "Erro de syntaxe! Formato esperado: 'Natureza da Categoria' :: 'Nome Categoria' : 'Nome Subcategoria'");
                            }

                            var split1 = fullName.split('::');
                            var split2 = split1[1].split(':');

                            category.name = split2[0].trim();
                            category.type = split1[0].trim();
                            for (var i in CategoryConstants.Types){
                                if (CategoryConstants.Types[i].name == category.type){
                                    category.type = CategoryConstants.Types[i].id;
                                }
                            }
                            category.subcategory = {name: split2[1].trim()};

                            openDialog($scope, $mdDialog, category).then(function(newCategory){

                                subcategory = new SubCategory(newCategory.subcategory);
                                subcategory.categoryId = newCategory.id;

                                category = new Category(newCategory);
                                delete category.subcategory;

                                if (category.id){
                                    subcategory.$new(function(sc){
                                        resolve(sc);
                                    }, function(err){
                                        reject("Não foi possível criar subcategoria")
                                    });
                                } else {
                                    category.$new(function(cat){
                                        subcategory.categoryId = cat.id;
                                        subcategory.$new(function(sc){
                                            resolve(sc);
                                        });
                                    }, function(err){
                                        reject("Não foi possível criar categoria/subcategoria")
                                    });
                                }
                            });

                        });

                        return promise;
                    }
                }

                function openDialog($scope, $mdDialog, category){
                    return $mdDialog.show({
                        controller: DialogController,
                        templateUrl: 'modules/categorization/views/new-subcategory-template.html',
                        parent: angular.element(document.body),
                        locals: {
                            category: category
                        },
                        clickOutsideToClose:true
                    })
                }

                function DialogController($scope, $mdDialog, category) {
                    $scope.category = category;
                    $scope.subCategoryTypes = CategoryConstants.Subcategory.Types;
                    $scope.categoryTypes = CategoryConstants.Types;


                    Category.listAll(function(categories){
                        $scope.categories = categories;

                        var found = false;
                        for(var i in categories){
                            if (categories[i].name == category.name){
                                if (category.type == categories[i].type){
                                    category.id = categories[i].id;
                                    found = true;
                                }
                            }
                        }

                        if (!found){
                            categories.push(category);
                        }
                    });

                    $scope.hide = function() {
                        $mdDialog.hide();
                    };
                    $scope.cancel = function() {
                        $mdDialog.cancel();
                    };
                    $scope.submit = function() {
                        $mdDialog.hide($scope.category);
                    };
                }
            }
        ])
});