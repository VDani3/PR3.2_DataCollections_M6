package cat.iesesteveterradas;
import org.bson.Document;

public class MongoDoc {
    private Document question;

    public MongoDoc(String id, String postTypeId, String acceptedAnswerId, String cDate, String score, String viewCount, String body, String ownerUserId, String lastDate, String title, String tags, String ansCount, String comCount, String license) {

        question = new Document("Id", id)
                        .append("PostTypeId", postTypeId)
                        .append("AcceptedAnswerId", acceptedAnswerId)
                        .append("CreationDate", cDate)
                        .append("Score", score)
                        .append("ViewCount", viewCount)
                        .append("Body", body)
                        .append("OwnerUserId", ownerUserId)
                        .append("LastActivityDate", lastDate)
                        .append("Title", title)
                        .append("Tags", tags)
                        .append("AnswerCount", ansCount)
                        .append("CommentCount", comCount)
                        .append("ContentLicense", license);

    }

    public Document getDocument() {
        return question;
    }
}
