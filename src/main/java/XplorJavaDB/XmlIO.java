package XplorJavaDB;

import com.google.common.primitives.Primitives;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XmlIO {

    public void write(Object a, String pathname) throws JAXBException {

        File file = new File(pathname);
        JAXBContext jaxbContext = JAXBContext.newInstance(a.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        // output pretty printed
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(a, file);
        jaxbMarshaller.marshal(a, System.out);
    }

    public <T> T read(String pathname, Class<T> targetClass) throws JAXBException {

        File file = new File(pathname);

        if (!file.exists()) {
            throw new IllegalArgumentException("File provided doesn't exist, and cannot deserialize: " + pathname);
        }

        JAXBContext context = JAXBContext.newInstance(targetClass);
        Unmarshaller deserializer = context.createUnmarshaller();

        Object result = deserializer.unmarshal(file);

        return Primitives.wrap(targetClass).cast(result);
    }
}