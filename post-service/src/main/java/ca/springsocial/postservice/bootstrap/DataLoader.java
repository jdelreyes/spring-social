package ca.springsocial.postservice.bootstrap;

import ca.springsocial.postservice.model.Post;
import ca.springsocial.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


// stereotype
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataLoader implements CommandLineRunner {
    //    repo
    private final MongoTemplate mongoTemplate;
    private final PostRepository postRepository;

    //    runs the app when booting up
    @Override
    public void run(String... args) throws Exception {
//        we can seed the database while booting up
        if (queryPostById("654d41fb68e15135137d7a75") == null) {
            Post widget = Post.builder()
                    .id("654d41fb68e15135137d7a75")
                    .title("Unleash Your Creative Genius: 5 Surprising Habits for Innovation \uD83D\uDE80\uD83D\uDCA1")
                    .content("\uD83D\uDE80 Elevate your creativity with these game-changing habits! \uD83D\uDCA1 Embrace" +
                            "boredom, diversify inputs, create a stimulating workspace, practice mindfulness, and " +
                            "celebrate failure. \uD83C\uDF08✨ What surprising habits fuel your creativity? Share " +
                            "your insights! #Creativity #Innovation #Inspiration")
                    .dateTimePosted(LocalDateTime.now())
                    .userId(1L)
                    .build();

            postRepository.save(widget);
        }
        if (queryPostById("654d41f968e15135137d7a74") == null) {
            Post widget = Post.builder()
                    .id("654d41f968e15135137d7a74")
                    .title("The Art of Learning: Mastering New Skills 🎨📚")
                    .content("Dive into the world of continuous learning! 🚀 Discover the secrets to mastering new" +
                            " skills, overcoming challenges, and embracing the journey of personal growth. 🌱 What" +
                            " skills are you currently mastering? Share your experiences!" +
                            " #Learning #PersonalGrowth #SkillBuilding")
                    .dateTimePosted(LocalDateTime.now())
                    .userId(2L)
                    .build();
            postRepository.save(widget);
        }

        if (queryPostById("654d759f35b2e07481068943") == null) {
            Post widget = Post.builder()
                    .id("654d759f35b2e07481068943")
                    .title("The Power of Gratitude: Transform Your Life ✨😊")
                    .content("Unlock the transformative power of gratitude! ✨ Embrace a positive mindset," +
                            " cultivate joy, and appreciate the beauty in everyday moments. 🌈 How has gratitude " +
                            "impacted your life? Share your stories! #Gratitude #Positivity #Mindfulness")
                    .dateTimePosted(LocalDateTime.now())
                    .userId(1L)
                    .build();
            postRepository.save(widget);
        }

        if (queryPostById("654d359f35b2e07481048375") == null) {
            Post widget = Post.builder()
                    .id("654d359f35b2e07481048375")
                    .title("Exploring Nature's Wonders: A Journey into the Wilderness 🌿🏞️")
                    .content("Embark on a journey into the heart of nature's beauty! 🌳 Experience the wonders of" +
                            " the wilderness, connect with the great outdoors, and discover the magic of untouched" +
                            " landscapes. ✨ What's your favorite nature escape? Share your favorite spots!" +
                            " #NatureLovers #Adventure #Wilderness")
                    .dateTimePosted(LocalDateTime.now())
                    .userId(3L)
                    .build();
            postRepository.save(widget);
        }

        if (queryPostById("6548359f35b2e07489283153") == null) {
            Post widget = Post.builder()
                    .id("6548359f35b2e07489283153")
                    .title("Tech Trends 2023: Navigating the Future of Innovation 🚀🔍")
                    .content("Stay ahead in the fast-paced world of technology! 🔮 Explore the latest trends," +
                            " innovations, and breakthroughs shaping the tech landscape in 2023. 💻 What tech" +
                            " trends are you most excited about? Share your insights!" +
                            " #TechTrends #Innovation #FutureTech")
                    .dateTimePosted(LocalDateTime.now())
                    .userId(2L)
                    .build();
            postRepository.save(widget);
        }

        if (queryPostById("654d759f35b2e84723069384") == null) {
            Post widget = Post.builder()
                    .id("654d759f35b2e84723069384")
                    .title("Culinary Adventures: Exploring Global Flavors 🌍🍜")
                    .content("Embark on a culinary journey around the world! 🍽️ Discover the richness" +
                            " of global flavors, indulge in diverse cuisines, and share your favorite " +
                            "international recipes. 🌮 What's your go-to global dish? Let's swap culinary " +
                            "stories! #Foodies #GlobalCuisine #CulinaryAdventures")
                    .dateTimePosted(LocalDateTime.now())
                    .userId(1L)
                    .build();
            postRepository.save(widget);
        }

    }

    public Post queryPostById(String value) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(value));
        return mongoTemplate.findOne(query, Post.class);
    }
}
