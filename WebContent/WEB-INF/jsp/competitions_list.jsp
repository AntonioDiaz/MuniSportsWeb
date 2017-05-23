<%@include file="taglibs.jsp"%>

<script>
	$(document).ready(function() {});	

	var app = angular.module('myApp', [ 'smart-table' ]);
	app.controller('myCtrl', [ '$scope', '$http', '$window',
		function($scope, $http, $window) {
			$scope.rowCollection = [];
			$scope.search = function doSearch() {
				var params = {
					params : {
						"idSport" : $scope.filterSport,
						"idCategory" : $scope.filterCategory
					}
				};
				$http.get("/server/search_competitions", params).success(function(response) {
					$scope.searchDone = true;
					$scope.rowCollection = response;
				})
			}
			
			$scope.viewCalendar = function fViewCalendar(idCompetition) {
				window.location.href = "/competitions/viewCalendar?idCompetition=" + idCompetition;
			}
			
			$scope.viewClassification = function fViewClassification(idCompetition) {
				window.location.href = "/competitions/viewClassification?idCompetition=" + idCompetition;
			}
			
			$scope.removeCompetition = function fRemoveCompetition(idCompetition) {
				var retVal = confirm("�Se va a borrar la competici�n y todos sus partidos desea continuar?");
				if (retVal) {
					window.location.href = "/competitions/doRemove?idCompetition=" + idCompetition;
				}
			}
		}
	]);	
</script>
<form  ng-app="myApp" ng-init="searchDone=false" ng-submit="search()" ng-controller="myCtrl" class="form-inline" >
	<div class="form-group" style="margin-right: 20px;">
		<label>Deporte</label>
		<select class="form-control" id="sport" ng-model="filterSport" >
			<option value="">&nbsp;</option>
			<c:forEach items="${sports}" var="sport">
				<option value="${sport.id}">${sport.name}</option>
			</c:forEach>
		</select>
	</div>
	<div class="form-group" style="margin-right: 20px;">
		<label>Categoria</label>
		<select class="form-control" id="category" ng-model="filterCategory" >
			<option value="">&nbsp;</option>
			<c:forEach items="${categories}" var="category">
				<option value="${category.id}">${category.name}</option>
			</c:forEach>
		</select>
	</div>			
	<div class="form-group">
		<button type="button" class="btn btn-default" ng-click="search()">Search</button>
	</div>
	<hr>	
	<table st-table="rowCollection" class="table table-striped">
		<thead>
			<tr>
				<th>Competici�n</th>
				<th>Deporte</th>
				<th>Categoria</th>
				<th>&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<tr ng-if="searchDone==false">
				<td colspan="4">
					Realize la busqueda
				</td>
			</tr>
			<tr ng-if="rowCollection.length<=0 && searchDone">
				<td colspan="4">
					No existe competiciones
				</td>
			</tr>
			<tr ng-repeat="row in rowCollection">
				<td>
					{{row.name}}
				</td>
				<td>
					{{row.sportEntity.name}}
				</td>
				<td>
					{{row.categoryEntity.name}}
				</td>
				<td>
					<div class="row">
						<div class="col-sm-4">
							<button type="button" class="btn btn-primary btn-block" ng-click="viewCalendar(row.id)">Calendario</button>
						</div>
						<div class="col-sm-4">
							<button type="button" class="btn btn-primary btn-block" ng-click="viewClassification(row.id)">Clasificaci�n</button>
						</div>
						<div class="col-sm-4">
							<button type="button" class="btn btn-warning btn-block" ng-click="removeCompetition(row.id)">Borrar</button>
						</div>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
</form>