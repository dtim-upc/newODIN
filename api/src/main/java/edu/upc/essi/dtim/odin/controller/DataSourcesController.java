package edu.upc.essi.dtim.odin.controller;

import edu.upc.essi.dtim.odin.models.mongo.DataSource;
import edu.upc.essi.dtim.odin.repository.DataSourcesRepository;
import edu.upc.essi.dtim.odin.services.filestorage.StorageService;
import edu.upc.essi.dtim.odin.services.impl.DataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dataSource")
public class DataSourcesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourcesController.class);
    private static final String LOG_MSG = "{} request finished with inputs: {} and return value: {}";
    private static final String EMPTY_INPUTS = "{}";

    @Autowired
    private DataSourcesRepository repository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private DataSourceService dataSourceService;


    @PostMapping( consumes = {"multipart/form-data"})
    public ResponseEntity<DataSource> createDataSources(@RequestPart DataSource dataSource, @RequestPart MultipartFile file, @RequestPart Boolean bootstrappingType) {
        try {

            DataSource _dataSource = dataSourceService.create(dataSource, bootstrappingType, file);


            String input = dataSource.toString().replaceAll("[\n\r\t]", "_");
            String returnval = _dataSource.toString().replaceAll("[\n\r\t]", "_");


            LOGGER.info(LOG_MSG, "createDataSources", input, returnval);
            return new ResponseEntity<>(_dataSource, HttpStatus.CREATED);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<List<DataSource>> getAllDataSources() {

        try {
            List<DataSource> dataSources = new ArrayList<>();

            repository.findAll().forEach(dataSources::add);

            if (dataSources.isEmpty()) {
                ResponseEntity response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
                LOGGER.info(LOG_MSG, "getAllDataSources", "", response);
                return response;
            }
            LOGGER.info(LOG_MSG, "getAllDataSources", EMPTY_INPUTS, "" );
            return new ResponseEntity<>(dataSources, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<DataSource> getAllDataSources(@PathVariable("id") String id) {

        try {
            Optional<DataSource> optionalDataSources = repository.findById(id);
            if (optionalDataSources.isPresent()) {
                LOGGER.info(LOG_MSG, "getDataSources", id, "" );
                return new ResponseEntity<>(optionalDataSources.get(), HttpStatus.OK);
            }
            LOGGER.info(LOG_MSG, "getDataSources", id, "" );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> editDataSources(@PathVariable("id") String id, @RequestBody DataSource dataSource) {
        try {
            Optional<DataSource> optionalDataSources = repository.findById(id);
            if (optionalDataSources.isPresent()) {
                DataSource ds = optionalDataSources.get();
                ds.setName(dataSource.getName());
                ds.setType(dataSource.getType());
                repository.save(ds);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDataSources(@PathVariable("id") String id) throws IOException {
        //Delete the file
        Optional<DataSource> optionalDataSource = repository.findById(id);
        if (optionalDataSource.isPresent()) {
            DataSource ds = optionalDataSource.get();
            storageService.delete(ds.getPath());
        }
        //Delete from MongoDB
        dataSourceService.delete(id);
        LOGGER.info(LOG_MSG, "deleteDataSources", id, HttpStatus.NO_CONTENT.toString() );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteDataSources() {
        try {
            repository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
