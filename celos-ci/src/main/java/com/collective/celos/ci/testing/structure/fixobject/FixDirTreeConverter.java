package com.collective.celos.ci.testing.structure.fixobject;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirTreeConverter {


    public FixDir transform(FixDir object, AbstractFixFileConverter converter) throws IOException {
        return (FixDir) transformInternal(object, converter);
    }

    private FixObject transformInternal(FixObject object, AbstractFixFileConverter converter) throws IOException {
        if (!object.isFile()) {
            FixDir fd = (FixDir) object;
            Map<String, FixObject> result = Maps.newHashMap();
            Map<String, FixObject> map = fd.getChildren();
            for(Map.Entry<String, FixObject> entry : map.entrySet()) {
                result.put(entry.getKey(), transformInternal(entry.getValue(), converter));
            }
            return new FixDir(result);
        } else {
            FixFile ff = (FixFile) object;
            return converter.convert(ff);
        }
    }

}
