package ch.zhaw.swengineering.model.persistence;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Greeting {

    public long id;
    public String content;

    public Greeting() {
        id = 0;
        content = null;
    }

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}