define(['./module',
        '../../categorization/services/subcategory-resources',
        '../../categorization/services/category-service',
        '../services/conciliation-resources',
        'modules/preferences/services/userpreferences-resources',
        '../services/account-resources'], function (app) {

	app.controller('ConciliationController', ['$scope', '$filter', '$mdDialog', 'AccountResource', 'SubCategoryResource', 'ConciliationResource', 'CategoryService', 'UserPreferencesResource',
        function($scope, $filter, $mdDialog, Account, SubCategory, Conciliation, CategoryService, UserPreferences) {
            $scope.appContext.contextMenu.setActions([]);

//            Account.listAll(function(accounts){
//                $scope.accounts = accounts;
//            })

            Account.listAllForTransferable(function (data){
                $scope.accounts = data;
            });

            SubCategory.listAll(function (subCategories){
                $scope.subCategories = subCategories;
            });

            $scope.userPreferences = new UserPreferences();
            $scope.userPreferences.$get(function(preferences){
                $scope.userPreferences = preferences;
            });

            $scope.newSubcategory = function(fullName, item){
                CategoryService.newSubcategoryShortcut($scope, fullName).then(function(newSubcategory){
                    SubCategory.listAll(function (subCategories){
                        $scope.subCategories = subCategories;

                        item.subCategory = newSubcategory;
                    });

                    addSuccess($scope);
                }, function(err){
                    addError($scope, err);
                });
            }

            $scope.querySearch = function(query){
                return $filter('filter')($scope.subCategories, query, false, 'fullName');
            }

            $scope.handleAccountSelection = function(){
                $scope.conciliation = null;
                $scope.account = null;
                $scope.finalBalance = null;

                new Account($scope.selectedAccount).$get(function(account){
                    $scope.account = account;
                    $scope.finalBalance = account.balance;
                }, function(err){
                    addError($scope, 'Não foi possível recuperar dados da conta.');
                });
            }

            $scope.loadFileEntries = function(conciliation){
                $scope.conciliation = new Conciliation(conciliation);
                $scope.account.conciliations.unshift(conciliation);

                checkInstallmentEntries($scope.conciliation.entries);
                updateBalancePosSync(conciliation)
            }

            function checkInstallmentEntries(entries){
                var found = false;

                if ($scope.userPreferences.updateInstallmentDate == null){
                    for (var i in entries){
                        conciliationEntry = entries[i];
                        if (conciliationEntry.installment){
                            found=true;
                            break;
                        }
                    }

                    if (found){
                        var confirm = $mdDialog.confirm()
                            .title('Compras parceladas')
                            .textContent('Encontrei compras parceladas nesta conciliação. Sou treinado para atualizar automaticamente a data do lançamento para que fique coerente com o momento que o dinheiro sai da sua conta. Gostaria que nas próximas importações eu fizesse isso?')
                            .ariaLabel('Ops!')
                            .ok('Sim, pode fazer!')
                            .cancel('Não!! Eu mesmo farei.');

                        $mdDialog.show(confirm).then(function(option) {
                            $scope.userPreferences.updateInstallmentDate = true;

                            $scope.userPreferences.$save(function(preferences){
                                $scope.userPreferences = preferences;
                            });
                        }, function() {
                            $scope.userPreferences.updateInstallmentDate = false;

                            $scope.userPreferences.$save(function(preferences){
                                $scope.userPreferences = preferences;
                            });
                        });
                    }
                }
            }

            $scope.delete = function(draftConciliation){
                new Conciliation(draftConciliation).$delete(function(){
                    for(var i in $scope.account.conciliations){
                        if ($scope.account.conciliations[i].id == draftConciliation.id){
                            $scope.account.conciliations.splice(i, 1);
                        }

                        if ($scope.conciliation && draftConciliation.id == $scope.conciliation.id){
                            $scope.conciliation = null;
                        }
                    }
                    addSuccess($scope);
                }, function(err){
                    addError($scope, 'Não foi possível remover a conciliação rascunho.');
                })
            }

            $scope.save = function(){
                $scope.conciliation = new Conciliation($scope.conciliation);
                $scope.conciliation.$save(function (conciliation){
                    $scope.conciliation = conciliation;
                    updateBalancePosSync(conciliation)
                    addSuccess($scope);
                }, function(err){
                    addError($scope, 'Não foi possível salvar os dados dessa conciliação.');
                })
            }

            $scope.sync = function(){
                // Check if all categories were informed
                var occur = 0;
                angular.forEach($scope.conciliation.entries, function(entry){
                    if (!entry.reject && entry.subCategory == null) {
                        occur++;
                    }
                })

                if (occur>0){
                    var confirm = $mdDialog.confirm()
                        .title('Gostaria de seguir com a sincronização?')
                        .textContent('Encontrei '+ occur +' linha'+(occur>1?'s':'')+' sem categoria. Gostaria que eu a'+(occur>1?'s':'')+' rejeitasse e seguisse com a sincronização ou prefere categorizá-la'+(occur>1?'s':'')+' agora?.')
                        .ariaLabel('Ops!')
                        .ok('Sim, prossiga!')
                        .cancel('Vou categorizá-las.');

                    $mdDialog.show(confirm).then(function() {
                      rejectAllNotCategorized();
                      syncIntoAccount();
                    }, function() {
                      return;
                    });
                } else {
                    syncIntoAccount();
                }

            }

            function syncIntoAccount (){
               $scope.conciliation.$syncIntoAccount(function(conciliation){
                    $scope.account.$get(function(account){
                        $scope.account = account;
                        $scope.conciliation = conciliation;

                        addSuccess($scope);
                    })

                }, function(err){
                    addError($scope, 'Não foi possível sincronizar os dados com a conta selecionada.');
                });
            }

            $scope.rollback = function(conciliationToRollback){
                new Conciliation(conciliationToRollback).$rollback(function(conciliation){
                    new Account($scope.selectedAccount).$get(function(account){
                        $scope.account = account;
                        updateBalancePosSync(conciliation)

                        addSuccess($scope);
                    });
                    $scope.conciliation = conciliation;

                }, function(err){
                    addError($scope, 'Não foi possível desfazer a importação dos dados relacionados a esta conciliação.');
                });
            }

            $scope.open = function(conciliationToOpen){
                new Conciliation(conciliationToOpen).$get(function(conciliation){
                    $scope.conciliation = conciliation;
                    updateBalancePosSync(conciliation);
                }, function(err){
                    addError($scope, 'Não foi possível abrir esta conciliação.');
                })
            }

            function updateBalancePosSync(conciliation){
                $scope.finalBalance = $scope.account.balance;

                if (!conciliation.imported){
                    angular.forEach(conciliation.entries, function(entry) {
                        if (!entry.reject) {
                            $scope.finalBalance += entry.amount;
                        }
                    });
                }
            }

            $scope.updateRejection = function(entry, toStatus){
                entry.reject = toStatus;

                if (entry.reject){
                    $scope.finalBalance -= entry.amount;
                } else {
                    $scope.finalBalance += entry.amount;
                }
            }

            function rejectAllNotCategorized(){
                angular.forEach($scope.conciliation.entries, function(entry){
                    if (entry.subCategory == null){
                        $scope.updateRejection(entry, true);
                    }
                })
            }
        }
	]);

    app.controller('UploadFileController', ['$scope', 'Upload',
        function($scope, Upload) {

            // upload on file select or drop
            $scope.upload = function (file) {
                if (file){
                    Upload.upload({
                        url: 'api/account/'+ $scope.account.id +'/conciliation/upload',
                        data: {file: file}
                    }).then(function (resp) {
                        // start the import process.
                        $scope.loadFileEntries(resp.data);
                    }, function (resp) {
                        addError($scope, 'Error status: ' + resp.status, '');
                    }, function (evt) {
                    });
                } else {
                    addError($scope, 'Tipo de arquivo inválido');
                }


            };
        }
    ]);
});

