var properties = [];

properties.push(require("./implementations/integration/IntegrationDataProperty"));
properties.push(require("./implementations/integration/IntegrationObjectProperty"));

properties.push(require("./implementations/sourceGraph/Property"));
properties.push(require("./implementations/sourceGraph/ContainerMembershipProperty"));

properties.push(require("./implementations/wrapper/Has_Attribute"));
properties.push(require("./implementations/wrapper/Has_Wrapper"));

properties.push(require("./implementations/Has_FeatureProperty"));
properties.push(require("./implementations/Has_RelationProperty"));
properties.push(require("./implementations/Part_OfProperty"));

properties.push(require("./implementations/OwlAllValuesFromProperty"));
properties.push(require("./implementations/OwlDatatypeProperty"));
properties.push(require("./implementations/OwlDeprecatedProperty"));
properties.push(require("./implementations/OwlDisjointWith"));
properties.push(require("./implementations/OwlEquivalentProperty"));
properties.push(require("./implementations/OwlFunctionalProperty"));
properties.push(require("./implementations/OwlInverseFunctionalProperty"));
properties.push(require("./implementations/OwlObjectProperty"));
properties.push(require("./implementations/OwlSomeValuesFromProperty"));
properties.push(require("./implementations/OwlSymmetricProperty"));
properties.push(require("./implementations/OwlTransitiveProperty"));
properties.push(require("./implementations/RdfProperty"));
properties.push(require("./implementations/RdfsSubClassOf"));
properties.push(require("./implementations/RdfsSubPropertyOf"));
properties.push(require("./implementations/SetOperatorProperty"));

var map = d3.map(properties, function (Prototype) {
	return new Prototype().type();
});

module.exports = function () {
	return map;
};
