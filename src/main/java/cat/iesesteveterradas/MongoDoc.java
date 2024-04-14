package cat.iesesteveterradas;
import org.bson.Document;

public class MongoDoc {
    private Document question;

    public MongoDoc(String id, String postTypeId, String acceptedAnswerId, String cDate, String score, String viewCount, String body, String ownerUserId, String lastDate, String title, String tags, String ansCount, String comCount, String license) {

        question = new Document("id", Integer.parseInt(id))
                        .append("postTypeId", postTypeId)
                        .append("acceptedAnswerId", acceptedAnswerId)
                        .append("creationDate", cDate)
                        .append("score", Integer.parseInt(score))
                        .append("viewCount", Integer.parseInt(viewCount))
                        .append("body", body)
                        .append("ownerUserId", ownerUserId)
                        .append("lastActivityDate", lastDate)
                        .append("title", title)
                        .append("tags", tags)
                        .append("answerCount", ansCount)
                        .append("commentCount", comCount)
                        .append("contentLicense", license);

    }

    public Document getDocument() {
        return question;
    }
}
