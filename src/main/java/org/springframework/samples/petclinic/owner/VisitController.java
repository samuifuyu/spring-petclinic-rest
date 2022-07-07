/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

	private final OwnerRepository owners;

	public VisitController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Get all visits for pet with petId with owner with ownerId
	 * @param ownerId
	 * @param petId
	 * @return
	 */
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits") // TODO
	public @ResponseBody Collection<Visit> initNewVisitForm(@PathVariable int ownerId, @PathVariable int petId) {
		return owners.findById(ownerId).getPet(petId).getVisits();
	}

	/**
	 * Add new visit for pet with petId with owner with ownerId
	 * @param ownerId
	 * @param petId
	 * @param visit
	 * @return
	 */
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits") // TODO
	@ResponseStatus(HttpStatus.CREATED)
	public @ResponseBody Owner processNewVisitForm(@PathVariable int ownerId, @PathVariable int petId,
			@Valid @RequestBody Visit visit) {
		Owner owner = owners.findById(ownerId);
		owner.addVisit(petId, visit);
		this.owners.save(owner);
		return owner;
	}

}
