package clarifai2.dto.feedback;

import clarifai2.Func1;
import clarifai2.api.request.feedback.Feedback;
import clarifai2.dto.input.Crop;
import clarifai2.internal.JSONAdapterFactory;
import clarifai2.internal.JSONArrayBuilder;
import clarifai2.internal.JSONObjectBuilder;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static clarifai2.internal.InternalUtil.assertJsonIs;
import static clarifai2.internal.InternalUtil.fromJson;
import static clarifai2.internal.InternalUtil.toJson;

@SuppressWarnings("NullableProblems")
@AutoValue
@JsonAdapter(RegionFeedback.Adapter.class)
public abstract class RegionFeedback {
  private Collection<ConceptFeedback> conceptFeedbacks = new ArrayList<>();
  private FaceFeedback faceFeedback = null;

  RegionFeedback() {} // AutoValue instances only

  @NotNull public static RegionFeedback make(@NotNull Crop crop, @NotNull Feedback feedback) {
    return new AutoValue_RegionFeedback(crop, feedback);
  }

  @NotNull public abstract Crop crop();
  @NotNull public abstract Feedback feedback();

  @NotNull public RegionFeedback withConceptFeedbacks(@NotNull ConceptFeedback... inputData) {
    return withConceptFeedbacks(Arrays.asList(inputData));
  }

  @NotNull public RegionFeedback withConceptFeedbacks(@NotNull Collection<ConceptFeedback> conceptFeedbacks) {
    this.conceptFeedbacks.addAll(conceptFeedbacks);
    return this;
  }

  @NotNull public RegionFeedback withFaceFeedback(@NotNull FaceFeedback faceFeedback) {
    this.faceFeedback = faceFeedback;
    return this;
  }

  static class Adapter extends JSONAdapterFactory<RegionFeedback> {
    @Nullable @Override protected Serializer<RegionFeedback> serializer() {
      return new Serializer<RegionFeedback>() {
        @NotNull @Override public JsonElement serialize(@Nullable RegionFeedback value, @NotNull final Gson gson) {
          if (value == null) {
            return JsonNull.INSTANCE;
          }
          final JSONObjectBuilder builder = new JSONObjectBuilder();
          JSONObjectBuilder plainRegion = builder
              .add("region_info", new JSONObjectBuilder()
                  .add("bounding_box", new JSONObjectBuilder()
                      .add("top_row", value.crop().top())
                      .add("left_col", value.crop().left())
                      .add("bottom_row", value.crop().bottom())
                      .add("right_col", value.crop().right()))
                  .add("feedback", value.feedback().toString()));
          JSONObjectBuilder dataObject = null;
          if (value.conceptFeedbacks.size() > 0 || value.faceFeedback != null) {
            dataObject = new JSONObjectBuilder();
            plainRegion.add("data", dataObject);
          }
          if (value.conceptFeedbacks.size() > 0) {
            dataObject
                .add("concepts", new JSONArrayBuilder()
                    .addAll(value.conceptFeedbacks, new Func1<ConceptFeedback, JsonElement>() {
                      @NotNull @Override public JsonElement call(@NotNull ConceptFeedback concept) {
                        return toJson(gson, concept, ConceptFeedback.class);
                      }
                    })
                );
          }
          if (value.faceFeedback != null) {
            dataObject.add("face", toJson(gson, value.faceFeedback, FaceFeedback.class));
          }
          return plainRegion.build();
        }
      };
    }

    @NotNull @Override protected TypeToken<RegionFeedback> typeToken() {
      return new TypeToken<RegionFeedback>() {};
    }
  }
}
