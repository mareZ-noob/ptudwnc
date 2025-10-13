package com.core.hw1.controller;

import com.core.hw1.exception.ResourceNotFoundException;
import com.core.hw1.model.Actor;
import com.core.hw1.repository.ActorRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actors")
@Tag(
        name = "Actor Controller",
        description = "REST APIs for Actor"
)
public class ActorController {

    /*
    http GET :8088/api/v1/actors
    http GET :8088/api/v1/actors/1
    http POST :8088/api/v1/actors firstName='SCARLETT' lastName='JOHANSSON'
    http PUT :8088/api/v1/actors/1 firstName='JANE' lastName='DOE'
    http DELETE :8088/api/v1/actors/201
    */

    private final ActorRepository actorRepository;

    public ActorController(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    @GetMapping
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable(value = "id") Short actorId) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + actorId));
        return ResponseEntity.ok().body(actor);
    }

    @PostMapping
    public Actor createActor(@RequestBody Actor actor) {
        return actorRepository.save(actor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable(value = "id") Short actorId, @RequestBody Actor actorDetails) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + actorId));

        // Update the found actor's details with the new data from the request body.
        actor.setFirstName(actorDetails.getFirstName());
        actor.setLastName(actorDetails.getLastName());

        // Save the updated actor back to the database.
        final Actor updatedActor = actorRepository.save(actor);
        return ResponseEntity.ok(updatedActor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable(value = "id") Short actorId) {
        // Find the actor to delete.
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + actorId));

        // The delete() method is provided by JpaRepository.
        actorRepository.delete(actor);

        return ResponseEntity.ok().build();
    }
}
