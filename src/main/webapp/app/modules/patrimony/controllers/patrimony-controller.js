define(['./module'
        ,'modules/patrimony/services/patrimony-resources'
        ,'../../shared/services/utils-service'
        ,'../../shared/services/constants'], function (app) {

	app.controller('PatrimonyController', ['$scope', '$location', '$filter', '$mdDialog', 'PatrimonyResource', 'Utils', 'Constants',
        function($scope, $location, $filter, $mdDialog, Patrimony, Utils, Constants) {
            $scope.appContext.contextMenu.setActions([]
            );

            $scope.productColors = ['#263238', '#455A64', '#607D8B', '#90A4AE', '#CFD8DC', '#9E9E9E', '#E0E0E0'];
            $scope.getProductCSS = function(index){
                return {'border-left': '6px solid '+ $scope.productColors[index]};
            }

            Patrimony.get(function(data){
                $scope.patrimony = data;

                updatePatrimonyHistoryChart();

                $scope.riskChart.series[0].data = [];
                for(var riskName in $scope.patrimony.riskMap){
                    $scope.riskChart.series[0].data.push({name: getRiskName(riskName), y:$scope.patrimony.riskMap[riskName]});
                }

                $scope.distributionChart.title.text = $filter('currency')($scope.patrimony.balanceInvested) + '<br>Total Investido';
                $scope.distributionChart.series[0].data = [];
                for(var riskName in $scope.patrimony.investTypeMap){
                    $scope.distributionChart.series[0].data.push({name: getProductName(riskName), y:$scope.patrimony.investTypeMap[riskName]});
                }

                $scope.liquidityChart.series[0].data = [];
                var total = 0;
                for(var date in $scope.patrimony.liquidityMap){
                    total += $scope.patrimony.liquidityMap[date];
                    $scope.liquidityChart.series[0].data.push({name: date, y:total});
                    $scope.liquidityChart.xAxis.categories.push($filter('date')(date, 'dd-MMM-yy').toUpperCase())
                }
            });

            function updatePatrimonyHistoryChart(){
                $scope.patrimonyEvolutionChart.series[0].data = [];
                $scope.patrimonyEvolutionChart.series[1].data = [];
                $scope.patrimonyEvolutionChart.series[2].data = [];
                $scope.patrimonyEvolutionChart.xAxis.categories = [];

                angular.forEach($scope.patrimony.history, function(history){
                    $scope.portfolioEvolutionChart.series[0].data.push(history.profitInvested)
                    $scope.portfolioEvolutionChart.series[1].data.push(history.percentProfitInvested*100)
                    $scope.portfolioEvolutionChart.xAxis.categories.push($filter('date')(history.date, 'MMM/yy').toUpperCase())

                    $scope.patrimonyEvolutionChart.series[0].data.push(history.profitTotal)
                    $scope.patrimonyEvolutionChart.series[1].data.push(history.percentProfitTotal*100)
                    $scope.patrimonyEvolutionChart.series[2].data.push(history.balanceTotal)
                    $scope.patrimonyEvolutionChart.xAxis.categories.push($filter('date')(history.date, 'MMM/yy').toUpperCase())
                });
                $scope.portfolioEvolutionChart.series[0].data.push($scope.patrimony.profitInvested)
                $scope.portfolioEvolutionChart.series[1].data.push($scope.patrimony.percentProfitInvested*100)
                $scope.portfolioEvolutionChart.xAxis.categories.push('Hoje')

                $scope.patrimonyEvolutionChart.series[0].data.push($scope.patrimony.profitTotal)
                $scope.patrimonyEvolutionChart.series[1].data.push($scope.patrimony.percentProfitTotal*100)
                $scope.patrimonyEvolutionChart.series[2].data.push($scope.patrimony.balanceTotal)
                $scope.patrimonyEvolutionChart.xAxis.categories.push('Hoje')
            }

            $scope.baseline = function(){
                angular.forEach($scope.patrimony.entries, function(entry){
                    delete entry.open;
                });

                new Patrimony($scope.patrimony).$baseline(function(data){
                        $scope.patrimony = data;
                        updatePatrimonyHistoryChart();
                });
            }

            $scope.detail = function(entry){
                $location.path('/account/'+ entry.assetType +'/'+ entry.accountId +'/detail');
            }

            function getRiskName(code){
                return Constants.ACCOUNT.INVESTMENT_RISK[code].name;
            }

            function getProductName(code){
                return Constants.ACCOUNT.INVESTMENT_PRODUCTS[code].name;
            }

            $scope.open = function(entry){
                entry.open = !entry.open
            }

            $scope.liquidityChart = {
                chart: {
                    type: 'area',
                      width: 850
                },
                title: {
                    text: 'Liquidez da Carteira'
                },
                xAxis: {
                    categories: []
                },
                yAxis: [{ // Primary yAxis
                  title: {
                      text: 'Valor (R$)'
                  }
                }],
                tooltip: {
                    formatter: function () {
                        return '<b>' + this.x + '</b><br/>' +
                                $filter('currency')(this.y) + '<br/>'
                    }
                },
                series: [ {
                    name: 'Valor Disponível',
                    data: [],
                    color: $scope.productColors[0]
                }],
                credits: {enabled: false},
                loading: false
            }

            $scope.portfolioEvolutionChart = {
                chart: {
                    type: 'spline',
                      width: 850
                },
                title: {
                    text: 'Evolução da Carteira'
                },
                xAxis: {
                    categories: []
                },
                yAxis: [{ // Primary yAxis
                    labels: {
                        format: '{value}'
                    },
                    title: {
                        text: 'Valor (R$)'
                    }
                }, { // Secondary yAxis
                title: {
                    text: 'Variação (%)'
                },
                labels: {
                    format: '{value}%'
                },
                opposite: true
                }],
                tooltip: {
                    formatter: function () {
                        return '<b>' + this.x + '</b><br/>' +
                                this.series.name + ': ' +
                                (this.series.name == 'Variação (%)' ? $filter('number')(this.y, 2) + '%' : $filter('currency')(this.y)) + '<br/>'
                    }
                },
                series: [ {
                    name: 'Variação (R$)',
                    type: 'column',
                    data: [],
                    color: $scope.productColors[4]
                },
                {
                    name: 'Variação (%)',
                    type: 'spline',
                    yAxis: 1,
                    data: [],
                    color: $scope.productColors[2]
                }
                ],
                credits: {enabled: false},
                loading: false
            }

            $scope.patrimonyEvolutionChart = {
                chart: {
                    type: 'spline',
                    plotBackgroundColor: '#FAFAFA',
                    backgroundColor: '#FAFAFA'
                },
                title: {
                    text: 'Evolução do Patrimônio'
                },
                xAxis: {
                    categories: []
                },
                yAxis: [{ // Primary yAxis
                    labels: {
                        format: '{value}'
                    },
                    title: {
                        text: 'Patrimônio (R$)'
                    }
                }, { // Secondary yAxis
                title: {
                    text: 'Variação (%)'
                },
                labels: {
                    format: '{value}%'
                },
                opposite: true
                }],
                tooltip: {
                    formatter: function () {
                        return '<b>' + this.x + '</b><br/>' +
                                this.series.name + ': ' +
                                (this.series.name == 'Variação (%)' ? $filter('number')(this.y, 2) + '%' : $filter('currency')(this.y)) + '<br/>'
                    }
                },
                series: [ {
                    name: 'Variação (R$)',
                    type: 'column',
                    data: [],
                    color: $scope.productColors[4]
                },
                {
                    name: 'Variação (%)',
                    type: 'spline',
                    yAxis: 1,
                    data: [],
                    color: $scope.productColors[2]
                },
                {
                    name: 'Patrimônio',
                    type: 'spline',
                    data: [],
                    color: $scope.productColors[0]
                }
                ],
                credits: {enabled: false},
                loading: false
            }


            $scope.distributionChart = {
                chart: {
                    type: 'pie',
                    width: 350
                },
                tooltip: {
                    enabled: true,
                    pointFormatter: function(){
                        return $filter('currency')(this.y)
                    }
                },
                plotOptions: {
                    series: {
                        dataLabels: {
                            enabled: true,
                            distance: -30,
                            formatter: function(){
                                return $filter('number')(this.percentage, 0) + '%';
                            }
                        }
                    },
                     pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        colors: $scope.productColors,
                        dataLabels: {
                            enabled: true
                        },
                        showInLegend: false,
                        label: {
                            enabled:true
                        },
                    }
                },
                title: {
                    text: '',
                    align: 'center',
                    verticalAlign: 'middle'
                },
                func: function(chart) {
                    $timeout(function() {
                        chart.reflow();
                    }, 0);
                },
                series: [{
                    name: 'Risco',
                    innerSize: '60%',
                    data: [
                    ]
                }],
                credits: {enabled: false},
                loading: false
            }

            $scope.riskChart = {
                chart: {
                    type: 'pie',
                    width: 400,
                    height: 300
                },
                tooltip: {
                    enabled: true,
                    pointFormatter: function(){
                        return $filter('currency')(this.y)
                    }
                },
                plotOptions: {
                    series: {
                        dataLabels: {
                            enabled: true,
                            distance: -30,
                            formatter: function(){
                                return this.point.name + ' - ' + $filter('number')(this.percentage, 0) + '%';
                            }
                        }
                    },
                     pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true
                        },
                        colors: $scope.productColors,
                        showInLegend: false,
                        label: {
                            enabled:true
                        }
                    }
                },
                title: {
                    text: 'Risco',
//                    align: 'center',
//                    verticalAlign: 'middle',
//                    y:130
                },
                func: function(chart) {
                    $timeout(function() {
                        chart.reflow();
                    }, 0);
                },
                series: [{
                    name: 'Risco',
//                    innerSize: '45%',
                    data: [
                    ]
                }],
                credits: {enabled: false},
                loading: false
            }

        }
	]);
});

