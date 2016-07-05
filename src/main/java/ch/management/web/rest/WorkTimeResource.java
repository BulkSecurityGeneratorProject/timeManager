package ch.management.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.management.domain.WorkTime;
import ch.management.repository.WorkTimeRepository;
import ch.management.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing WorkTime.
 */
@RestController
@RequestMapping("/api")
public class WorkTimeResource {

    private final Logger log = LoggerFactory.getLogger(WorkTimeResource.class);
        
    @Inject
    private WorkTimeRepository workTimeRepository;
    
    /**
     * POST  /work-times : Create a new workTime.
     *
     * @param workTime the workTime to create
     * @return the ResponseEntity with status 201 (Created) and with body the new workTime, or with status 400 (Bad Request) if the workTime has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/work-times",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WorkTime> createWorkTime(@Valid @RequestBody WorkTime workTime) throws URISyntaxException {
        log.debug("REST request to save WorkTime : {}", workTime);
        if (workTime.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("workTime", "idexists", "A new workTime cannot already have an ID")).body(null);
        }
        WorkTime result = workTimeRepository.save(workTime);
        return ResponseEntity.created(new URI("/api/work-times/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("workTime", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /work-times : Updates an existing workTime.
     *
     * @param workTime the workTime to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated workTime,
     * or with status 400 (Bad Request) if the workTime is not valid,
     * or with status 500 (Internal Server Error) if the workTime couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/work-times",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WorkTime> updateWorkTime(@Valid @RequestBody WorkTime workTime) throws URISyntaxException {
        log.debug("REST request to update WorkTime : {}", workTime);
        if (workTime.getId() == null) {
            return createWorkTime(workTime);
        }
        WorkTime result = workTimeRepository.save(workTime);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("workTime", workTime.getId().toString()))
            .body(result);
    }

    /**
     * GET  /work-times : get all the workTimes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of workTimes in body
     */
    @RequestMapping(value = "/work-times",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<WorkTime> getAllWorkTimes() {
        log.debug("REST request to get all WorkTimes");
        List<WorkTime> workTimes = workTimeRepository.findAll();
        return workTimes;
    }

    /**
     * GET  /work-times/:id : get the "id" workTime.
     *
     * @param id the id of the workTime to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the workTime, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/work-times/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<WorkTime> getWorkTime(@PathVariable Long id) {
        log.debug("REST request to get WorkTime : {}", id);
        WorkTime workTime = workTimeRepository.findOne(id);
        return Optional.ofNullable(workTime)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /work-times/:id : delete the "id" workTime.
     *
     * @param id the id of the workTime to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/work-times/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWorkTime(@PathVariable Long id) {
        log.debug("REST request to delete WorkTime : {}", id);
        workTimeRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("workTime", id.toString())).build();
    }

}
