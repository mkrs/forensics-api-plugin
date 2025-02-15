package io.jenkins.plugins.forensics.reference;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import hudson.model.Run;

import io.jenkins.plugins.bootstrap5.MessagesViewModel;
import io.jenkins.plugins.util.JenkinsFacade;

import static io.jenkins.plugins.forensics.assertions.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ReferenceBuild}.
 *
 * @author Ullrich Hafner
 */
class ReferenceBuildTest {
    private static final List<String> MESSAGES = Arrays.asList("Message 1", "Message 2");
    private static final String ID = "ID";

    @Test
    void shouldAttachBuild() {
        Run<?, ?> currentBuild = mock(Run.class);

        ReferenceBuild referenceBuild = new ReferenceBuild(currentBuild, MESSAGES);
        assertThat(referenceBuild).hasOwner(currentBuild);

        Run<?, ?> newBuild = mock(Run.class);
        referenceBuild.onAttached(newBuild);
        assertThat(referenceBuild).hasOwner(newBuild);
        referenceBuild.onLoad(currentBuild);
        assertThat(referenceBuild).hasOwner(currentBuild);
    }

    @Test
    void shouldHandleMissingReferenceBuild() {
        Run<?, ?> currentBuild = mock(Run.class);

        ReferenceBuild referenceBuild = new ReferenceBuild(currentBuild, MESSAGES);

        assertThat(referenceBuild).doesNotHaveReferenceBuild();
        assertThat(referenceBuild).hasOnlyMessages(MESSAGES);
        assertThat(referenceBuild).hasOwner(currentBuild);
        assertThat(referenceBuild).hasReferenceBuildId(ReferenceBuild.NO_REFERENCE_BUILD);
        assertThat(referenceBuild).hasReferenceLink(
                "Reference build '-' not found anymore - maybe the build has been renamed or deleted?");
        assertThat(referenceBuild.getReferenceBuild()).isEmpty();

        assertThat(referenceBuild.getIconFileName()).isNull();
        assertThat(referenceBuild.getDisplayName()).isNull();
        assertThat(referenceBuild.getUrlName()).isEqualTo(ReferenceBuild.REFERENCE_DETAILS_URL);

        assertThat(referenceBuild.getTarget()).isInstanceOfSatisfying(MessagesViewModel.class,
                model -> {
                    assertThat(model.getDisplayName()).isEqualTo("Reference build - Messages");
                    assertThat(model.getErrorMessages()).isEmpty();
                    assertThat(model.getInfoMessages()).containsAll(MESSAGES);
                });
    }

    @Test
    void shouldHandleValidReferenceBuild() {
        Run<?, ?> currentBuild = mock(Run.class);
        Run<?, ?> targetBuild = mock(Run.class);
        when(targetBuild.getExternalizableId()).thenReturn(ID);

        ReferenceBuild referenceBuild = new ReferenceBuild(currentBuild, MESSAGES, targetBuild);

        assertThat(referenceBuild).hasReferenceBuild();
        assertThat(referenceBuild).hasOnlyMessages(MESSAGES);
        assertThat(referenceBuild).hasOwner(currentBuild);
        assertThat(referenceBuild).hasReferenceBuildId(ID);

        assertThat(referenceBuild.getIconFileName()).isNull();
        assertThat(referenceBuild.getDisplayName()).isNull();
        assertThat(referenceBuild.getUrlName()).isEqualTo(ReferenceBuild.REFERENCE_DETAILS_URL);
    }

    @Test
    void shouldHandleValidReferenceBuildId() {
        Run<?, ?> currentBuild = mock(Run.class);
        Run<?, ?> targetBuild = mock(Run.class);

        JenkinsFacade jenkinsFacade = mock(JenkinsFacade.class);
        when(jenkinsFacade.getBuild(ID)).thenReturn(Optional.of(targetBuild));
        when(jenkinsFacade.getAbsoluteUrl(any())).thenReturn("URL");

        when(targetBuild.getFullDisplayName()).thenReturn("Name");
        ReferenceBuild referenceBuild = new ReferenceBuild(currentBuild, MESSAGES, ID, jenkinsFacade);

        assertThat(referenceBuild).hasReferenceBuild();
        assertThat(referenceBuild).hasOnlyMessages(MESSAGES);
        assertThat(referenceBuild).hasOwner(currentBuild);
        assertThat(referenceBuild).hasReferenceBuildId(ID);
        assertThat(referenceBuild).hasReferenceLink("<a href=\"URL\" class=\"model-link inside\">Name</a>");
        assertThat(referenceBuild.getReferenceBuild()).contains(targetBuild);

        assertThat(referenceBuild.getIconFileName()).isNull();
        assertThat(referenceBuild.getDisplayName()).isNull();
        assertThat(referenceBuild.getUrlName()).isEqualTo(ReferenceBuild.REFERENCE_DETAILS_URL);
    }
}
