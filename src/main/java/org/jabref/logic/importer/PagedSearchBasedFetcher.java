package org.jabref.logic.importer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jabref.logic.JabRefException;
import org.jabref.logic.importer.fetcher.transformators.AbstractQueryTransformer;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.paging.Page;

public interface PagedSearchBasedFetcher extends SearchBasedFetcher {

    /**
     * @param transformedQuery the complex query defining all fielded search parameters
     * @param pageNumber       requested site number indexed from 0
     * @return Page with search results
     */
    Page<BibEntry> performSearchPagedForTransformedQuery(String transformedQuery, int pageNumber, AbstractQueryTransformer transformer) throws FetcherException;

    /**
     * @param searchQuery query string that can be parsed into a complex search query
     * @param pageNumber  requested site number indexed from 0
     * @return Page with search results
     */
    default Page<BibEntry> performSearchPaged(String searchQuery, int pageNumber) throws JabRefException {
        if (searchQuery.isBlank()) {
            return new Page<>(searchQuery, pageNumber, Collections.emptyList());
        }
        AbstractQueryTransformer transformer = getQueryTransformer();
        Optional<String> transformedQuery = transformer.parseQueryStringIntoComplexQuery(searchQuery);
        // Otherwise just use query as a default term
        return this.performSearchPagedForTransformedQuery(transformedQuery.orElse(""), pageNumber, transformer);
    }

    /**
     * @return default pageSize
     */
    default int getPageSize() {
        return 20;
    }

    @Override
    default List<BibEntry> performSearchForTransformedQuery(String transformedQuery, AbstractQueryTransformer transformer) throws FetcherException {
        return new ArrayList<>(performSearchPagedForTransformedQuery(transformedQuery, 0, transformer).getContent());
    }
}
