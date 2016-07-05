package ch.management.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.management.domain.Worker;
import ch.management.repository.WorkerRepository;
import ch.management.service.CalculationService;
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
 * REST controller for managing Worker.
 */
@RestController
@RequestMapping("/api")
public class WorkerResource {

    private final Logger log = LoggerFactory.getLogger(WorkerResource.class);
        
    @Inject
    private WorkerRepository workerRepository;
    
    @Inject
    private CalculationService calculationService;
    
    /**
     * POST  /workers : Create a new worker.
     *
     * @param worker the worker to create
     * @return the ResponseEntity with status 201 (Created) and with body the new worker, or with status 400 (Bad Request) if the worker has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/workers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Worker> createWorker(@Valid @RequestBody Worker worker) throws URISyntaxException {
        log.debug("REST request to save Worker : {}", worker);
        if (worker.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("worker", "idexists", "A new worker cannot already have an ID")).body(null);
        }
        Worker result = workerRepository.save(worker);
        return ResponseEntity.created(new URI("/api/workers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("worker", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /workers : Updates an existing worker.
     *
     * @param worker the worker to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated worker,
     * or with status 400 (Bad Request) if the worker is not valid,
     * or with status 500 (Internal Server Error) if the worker couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/workers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Worker> updateWorker(@Valid @RequestBody Worker worker) throws URISyntaxException {
        log.debug("REST request to update Worker : {}", worker);
        if (worker.getId() == null) {
            return createWorker(worker);
        }
        Worker result = workerRepository.save(worker);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("worker", worker.getId().toString()))
            .body(result);
    }

    /**
     * GET  /workers : get all the workers.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of workers in body
     */
    @RequestMapping(value = "/workers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Worker> getAllWorkers() {
        log.debug("REST request to get all Workers");
        calculationService.calculateWorkerWorkHours();
        List<Worker> workers = workerRepository.findAll();
        return workers;
    }

    /**
     * GET  /workers/:id : get the "id" worker.
     *
     * @param id the id of the worker to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the worker, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/workers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Worker> getWorker(@PathVariable Long id) {
        log.debug("REST request to get Worker : {}", id);
        Worker worker = workerRepository.findOne(id);
        return Optional.ofNullable(worker)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /workers/:id : delete the "id" worker.
     *
     * @param id the id of the worker to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/workers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        log.debug("REST request to delete Worker : {}", id);
        workerRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("worker", id.toString())).build();
    }

}
