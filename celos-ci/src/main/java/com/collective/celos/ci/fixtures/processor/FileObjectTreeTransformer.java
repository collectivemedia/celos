package com.collective.celos.ci.fixtures.processor;

import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FileObjectTreeTransformer {


    public FixDir transform(FixDir object, AbstractFixFileConverter converter) throws IOException {
        return (FixDir) transformInternal(object, converter);
    }

    private FixObject transformInternal(FixObject object, AbstractFixFileConverter converter) throws IOException {
        if (object.isDirectory()) {
            FixDir fd = (FixDir) object;
            Map<String, FixObject> result = Maps.newHashMap();
            for(Map.Entry<String, FixObject> entry : fd.getChildren().entrySet()) {
                result.put(entry.getKey(), transformInternal(entry.getValue(), converter));
            }
            return new FixDir(result);
        } else {
            FixFile ff = (FixFile) object;
            return converter.convert(ff);
        }
    }

}
