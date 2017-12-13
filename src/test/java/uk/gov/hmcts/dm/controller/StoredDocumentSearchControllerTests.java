package uk.gov.hmcts.dm.controller;

import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.gov.hmcts.dm.commandobject.MetadataSearchCommand;
import uk.gov.hmcts.dm.componenttests.ComponentTestBase;
import uk.gov.hmcts.dm.domain.StoredDocument;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by pawel on 09/11/2017.
 */
public class StoredDocumentSearchControllerTests extends ComponentTestBase {

    @Test
    public void testValidCommandAndSearchReturn3Documents() throws Exception {

        MetadataSearchCommand searchCommand = new MetadataSearchCommand("name", "thename");

        List<StoredDocument> documents = Arrays.asList(
                new StoredDocument(),
                new StoredDocument(),
                new StoredDocument());

        Pageable pageable = new PageRequest(0, 2);

        when(
            this.searchService
                .findStoredDocumentsByMetadata(eq(searchCommand), any(Pageable.class)))
        .thenReturn(new PageImpl<>(documents, pageable, 3));

        restActions
            .withAuthorizedUser("userId")
            .withAuthorizedService("divorce")
        .post("/documents/filter", searchCommand)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.size", is(2)))
            .andExpect(jsonPath("$.page.totalElements", is(3)))
            .andExpect(jsonPath("$.page.totalPages", is(2)))
            .andExpect(jsonPath("$.page.number", is(0)))
            .andExpect(jsonPath("$._links.first.href", is("http://localhost/documents/filter?page=0&size=2")))
            .andExpect(jsonPath("$._links.self.href", is("http://localhost/documents/filter?page=0&size=2")))
            .andExpect(jsonPath("$._links.next.href", is("http://localhost/documents/filter?page=1&size=2")))
            .andExpect(jsonPath("$._links.last.href", is("http://localhost/documents/filter?page=1&size=2")));

    }

    @Test
    public void testInValidCommandAnd() throws Exception {

        MetadataSearchCommand searchCommand = new MetadataSearchCommand("thename", null);

        restActions
                .withAuthorizedUser("userId")
                .withAuthorizedService("divorce")
                .post("/documents/filter", searchCommand)
                .andExpect(status().is4xxClientError());

    }

}
