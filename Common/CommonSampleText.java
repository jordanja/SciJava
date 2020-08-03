package thesis.Common;

public class CommonSampleText {
    private static String text = "One of the outstanding qualities of the film “Witness”, directed by Peter Weir is the plethora of interesting ideas based on the contrasting aspects between the two conflicting cultures presented, the Amish and the English. These ideas incorporate the clash of contradictory cultures, the choice of pacifism over violence, and forbidden love caused by cultural difference. These are presented effectively using a variety of film techniques and genres that make the film engaging for viewers. The clash of the Amish and English cultures is the overarching idea of the film. The film commences with a panoramic view of long, thick grass with the Amish community emerging from the landscape accompanied by natural and ambient lighting, appearing organically connected to their surrounds. This effectively establishes the Amish as peaceful people who live in harmony and without technology. The montage of the Amish horse and cart mode of transport contrasted with modern English cars zooming past on the main road highlights the differentiation between them. The Amish world ignores modern progress as the past is celebrated and the old ways satisfy their needs. However, this view of the Amish is soon shattered in the next scene through their interaction with the heterogeneous English. Weir employs a long shot of a train station with civilians consumed by their own activities, with harsh artificial lighting symbolic of the urban world. These techniques serve to define the anonymity and artificiality of Detective John Book’s world. Book’s arrival at the Lapp farm by motor vehicle juxtaposes the complexity and danger of his environs against a backdrop of Amish simplicity and purity, establishing the distinctiveness of the urban American and agrarian Amish worlds. In addition, Weir uses clever symbolism when Book crashes his car into a birdhouse on arrival at the Lapp farm. This accident suggests Book introducing violence from his contemporary society and destroying the serenity of the Amish culture. Clearly, Weir criticises 20th century contemporary society that clashes with the peaceful Amish culture, an idea that engages viewers who examine both cultures with new awareness. The Amish choice of pacifism over typical English employment of violence is another important idea that many viewers would find interesting and unusual, Weir effectively portraying and presenting two very different responses to situations. In the murder scene at the train station, a close-up shot of Samuel’s eyes emphasise his wide-eyed innocence compared to the callousness of the English around him. This image is juxtaposed by cutting to close-up shots of the brutal slitting of the victim’s throat in the bathroom, accompanied by violent diegetic sounds such as grunts and thuds, seen subjectively from Samuel’s perspective. This clearly shows that the western world engages in violence while the nature of the Amish is innocent pacifists. Furthermore, in the scene with American tourists, Weir uses midshots to show the Amish’s peaceful rejection of this group. However, this is juxtaposed with Book’s violent threatening dialogue, “Listen lady, if you take that picture I’ll rip your brassiere off and strangle you with it”; concisely, Weir has shown that contemporary society adopts a violent response to problems in contrast with the Amish’s peaceful alternative. Weir cleverly employs colour to suggest the atmosphere of violence throughout the film. Light blue is traditionally symbolic of tranquility, happiness and freedom: Book’s car is light blue, as is Samuel’s Amish clothing when running through the fields to escape the wrath of Schaeffer and McFee. Significantly, these men who have been entrusted to maintain fairness and freedom in their world, have discarded the blue shirts of their profession to don suits of ruthless businessmen. Moreover, the symbolism of the gun, signifying the violence of the western world, is hidden in a drawer when Book settles into the Lapp farm. It only reappears when Book reestablishes contact with his American society, emphasizing that violence has no place in Amish culture. Weir presents the conflict between Amish pacifism and American violence to engage audiences to choose better solutions when faced with societal problems. The consummation of love across the cultural divide is forbidden, effectively conveyed through the mise-en-scene to further accentuate the conflicting and incompatible aspects of the two societies, as well as being a provocative genre for viewers. In the barn-dancing scene, the lyrics of the background music of Sam Cooke’s “What a Wonderful World it would be”, raises sexual tension emphasizing the difference between love and reason. A motif of light pervades the scene with a long shot of Book’s vehicle with its headlights on framing the dancing couple. Weir employs panning shots that encircle the couple as they move closer and closer together. Their physical proximity is interrupted by the zealous Eli who carries a lamp with him. Eli’s lamp shifts the couple back into the restrained world of the Amish. Book’s downward glance emphasises his acknowledgement that his love for Rachel is forbidden by cultural differences. During the film, he constantly views Rachel through barred or netted windows, which accentuates that he is a mere observer in Amish society; hence the love between them cannot eventuate. Equilibrium of heart and mind is achieved in the attraction between Book and Rachel, represented by Rachel deliberately removing her bonnet, symbolic of her rigid religious beliefs, after she recognizes and accepts her feelings for Book. At the conclusion of the movie, Rachel stands alongside the Amish house with shadows in the backdrop, while Book is shown in a mid-shot with a road behind him, leaving the farm. Clearly, this illustrates that Book’s love for Rachel remains unrequited and he should take a different path, back to the western culture to which he belongs. Weir diverges from the norm by having lovers separate, frustratingly but realistically, just as they finally attain momentary bliss, engaging audiences due to its unexpected nature. After close analysis, Peter Weir’s “Witness” is seen to successfully interest viewers because it portrays intriguing ideas about the conflicting societies of the Amish and English. The depiction of ideas about cross-cultural clashes, innocence opposed to brutality and forbidden love effectively appeal to modern audiences. Without doubt, Weir has demonstrated that the Amish and western society cannot operate in harmony, thus providing the viewer with an unusual dual narrative that engages them.";
    public static String getStringMessage(int numWords) {
        String[] arr = text.split("\\s+"); 
     
        String strToReturn="";

        for (int wordCount = 0; wordCount < Integer.min(numWords, arr.length); wordCount++) {
            strToReturn = strToReturn + " " + arr[wordCount];         
        }
       return strToReturn;
    }
}