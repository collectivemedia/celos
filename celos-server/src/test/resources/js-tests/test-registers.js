importPackage(Packages.org.junit);

var b1 = "b1-Iñtërnâtiônàlizætiøn";
var k1 = "k1-Iñtërnâtiônàlizætiøn";
var k2 = "k2-Iñtërnâtiônàlizætiøn";
var v1 = { foo: "bar-Iñtërnâtiônàlizætiøn" };
var v2 = { quux: "meh-Iñtërnâtiônàlizætiøn" };

Assert.assertNull(celos.getRegister(b1, k1));
Assert.assertNull(celos.getRegister(b1, k2));
Assert.assertEquals(JSON.stringify({}), JSON.stringify(getRegistersAsMap(b1)));

celos.putRegister(b1, k1, v1);
Assert.assertEquals(JSON.stringify(v1), JSON.stringify(celos.getRegister(b1, k1)));
Assert.assertNull(celos.getRegister(b1, k2));
Assert.assertEquals(JSON.stringify({ "k1-Iñtërnâtiônàlizætiøn": v1 }), JSON.stringify(getRegistersAsMap(b1)));

celos.putRegister(b1, k2, v2);
Assert.assertEquals(JSON.stringify(v1), JSON.stringify(celos.getRegister(b1, k1)));
Assert.assertEquals(JSON.stringify(v2), JSON.stringify(celos.getRegister(b1, k2)));
Assert.assertEquals(JSON.stringify({ "k1-Iñtërnâtiônàlizætiøn": v1, "k2-Iñtërnâtiônàlizætiøn": v2 }), JSON.stringify(getRegistersAsMap(b1)));

celos.deleteRegister(b1, k1);
Assert.assertNull(celos.getRegister(b1, k1));
Assert.assertEquals(JSON.stringify(v2), JSON.stringify(celos.getRegister(b1, k2)));
Assert.assertEquals(JSON.stringify({ "k2-Iñtërnâtiônàlizætiøn": v2 }), JSON.stringify(getRegistersAsMap(b1)));

celos.deleteRegister(b1, k2);
Assert.assertNull(celos.getRegister(b1, k1));
Assert.assertNull(celos.getRegister(b1, k2));
Assert.assertEquals(JSON.stringify({}), JSON.stringify(getRegistersAsMap(b1)));

celos.putRegister(b1, k1, v1);
Assert.assertEquals(JSON.stringify(v1), JSON.stringify(celos.getRegister(b1, k1)));
Assert.assertNull(celos.getRegister(b1, k2));
Assert.assertEquals(JSON.stringify({ "k1-Iñtërnâtiônàlizætiøn": v1 }), JSON.stringify(getRegistersAsMap(b1)));

function getRegistersAsMap(bucket) {
    var map = {};
    celos.forEachRegister(bucket, function(k, v) {
        map[k] = v;
    });
    return map;
}