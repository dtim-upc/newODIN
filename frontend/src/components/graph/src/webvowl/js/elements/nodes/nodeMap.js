var nodes = [];
nodes.push(require("./implementations/integration/IntegrationClass"));
nodes.push(require("./implementations/integration/IntegrationData"));
nodes.push(require("./implementations/integration/IntegrationObject"));

nodes.push(require("./implementations/sourceGraph/RDF_Class"));
nodes.push(require("./implementations/sourceGraph/Seq"));

nodes.push(require("./implementations/wrappers/Attribute"));
nodes.push(require("./implementations/wrappers/DataSource"));
nodes.push(require("./implementations/wrappers/Wrapper"));



nodes.push(require("./implementations/ConceptGClass"));
nodes.push(require("./implementations/FeatureG_ID_Class"));
nodes.push(require("./implementations/FeatureGClass"));

nodes.push(require("./implementations/ExternalClass"));
nodes.push(require("./implementations/OwlClass"));
nodes.push(require("./implementations/OwlComplementOf"));
nodes.push(require("./implementations/OwlDeprecatedClass"));
nodes.push(require("./implementations/OwlDisjointUnionOf"));
nodes.push(require("./implementations/OwlEquivalentClass"));
nodes.push(require("./implementations/OwlIntersectionOf"));
nodes.push(require("./implementations/OwlNothing"));
nodes.push(require("./implementations/OwlThing"));
nodes.push(require("./implementations/OwlUnionOf"));
nodes.push(require("./implementations/RdfsClass"));
nodes.push(require("./implementations/RdfsDatatype"));
nodes.push(require("./implementations/RdfsLiteral"));
nodes.push(require("./implementations/RdfsResource"));

var map = d3.map(nodes, function (Prototype) {
	// return new Prototype().type();
	if(new Prototype().guiLabel() === undefined)
		return new Prototype().type();
	return new Prototype().guiLabel();
});

module.exports = function () {
	return map;
};
