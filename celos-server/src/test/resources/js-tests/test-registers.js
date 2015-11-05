importPackage(Packages.org.junit);

var b1 = "b1-Iñtërnâtiônàlizætiøn";
var b2 = "b2-Iñtërnâtiônàlizætiøn";
var b3 = "b3-Iñtërnâtiônàlizætiøn";
var k1 = "k1-Iñtërnâtiônàlizætiøn";
var k2 = "k2-Iñtërnâtiônàlizætiøn";
var k3 = "k3-Iñtërnâtiônàlizætiøn";
var v1 = { foo: "bar-Iñtërnâtiônàlizætiøn" };
var v2 = { quux: "meh-Iñtërnâtiônàlizætiøn" };
var v3 = { bla: "baz-Iñtërnâtiônàlizætiøn" };

Assert.assertNull(celos.getRegister(b1, k3));
Assert.assertNull(celos.getRegister(b2, k1));
Assert.assertEquals(JSON.stringify({}), JSON.stringify(getRegistersAsMap(b3)));

Assert.assertEquals(JSON.stringify(v1), JSON.stringify(celos.getRegister(b1, k1)));
Assert.assertEquals(JSON.stringify(v2), JSON.stringify(celos.getRegister(b1, k2)));
Assert.assertEquals(JSON.stringify(v3), JSON.stringify(celos.getRegister(b2, k3)));

Assert.assertEquals(JSON.stringify({ "k1-Iñtërnâtiônàlizætiøn": v1, "k2-Iñtërnâtiônàlizætiøn": v2 }), JSON.stringify(getRegistersAsMap(b1)));
Assert.assertEquals(JSON.stringify({ "k3-Iñtërnâtiônàlizætiøn": v3 }), JSON.stringify(getRegistersAsMap(b2)));

function getRegistersAsMap(bucket) {
    var map = {};
    celos.forEachRegister(bucket, function(k, v) {
        map[k] = v;
    });
    return map;
}