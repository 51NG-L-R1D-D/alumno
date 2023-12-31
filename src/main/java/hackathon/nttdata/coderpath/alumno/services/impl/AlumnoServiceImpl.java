package hackathon.nttdata.coderpath.alumno.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.MediaType.*;
import org.springframework.stereotype.Service;

import static org.springframework.web.reactive.function.BodyInserters.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.google.common.collect.Maps;

import hackathon.nttdata.coderpath.alumno.config.ApplicationConfiguration;
import hackathon.nttdata.coderpath.alumno.documents.Alumno;
import hackathon.nttdata.coderpath.alumno.documents.dtowebclient.Cursos;
import hackathon.nttdata.coderpath.alumno.repository.AlumnoRepository;
import hackathon.nttdata.coderpath.alumno.services.AlumnoService;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

	private final AlumnoRepository alumnoRepository;
	private final ApplicationConfiguration configuration;

	@Autowired
	private WebClient client;

	private Mono<Cursos> getCursosById(String id) {
		Mono<Cursos> curso =

				client.get().uri("/id/{id}", id).retrieve().bodyToMono(Cursos.class);
		return curso;

	}

	public Mono<ServerResponse> getOne(ServerRequest request, String idx) {

		String id = request.pathVariable(idx);

		return this.findCursosById(id).flatMap(c -> ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).syncBody(c)
				.switchIfEmpty(ServerResponse.notFound().build()));
	}

	@Override
	public Mono<Alumno> findById(String id) {
		// TODO Auto-generated method stub
		return alumnoRepository.findById(id);
	}

	@Override
	public Flux<Alumno> findAlumonos() {
		// TODO Auto-generated method stub
		return alumnoRepository.findAll();
	}

	@Override
	public Mono<Alumno> saveAlumno(Alumno document) {
		// TODO Auto-generated method stub
		return alumnoRepository.save(document);
	}

	@Override
	public Mono<Void> deleteAlumno(Alumno document) {
		// TODO Auto-generated method stub
		return alumnoRepository.delete(document);
	}

	@Override
	public Map<String, Object> balanceadorTest() {
		// TODO Auto-generated method stub
		Map<String, Object> response = Maps.newHashMap();
		response.put("balanceador", configuration.getBalanceadorTest());
		response.put("personal_asset", findAlumonos());
		return response;
	}

	@Override
	public Map<String, Object> rutaWebClientTest() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Map<String, Object> response = Maps.newHashMap();
		response.put("balanceador", configuration.getUrlCursos());
		response.put("personal_asset", findCursos());
		return response;
	}

	@Override
	public Flux<Cursos> findCursos() {
		// TODO Auto-generated method stub
		System.out.println("ruta de cursos: " + client.toString());
		return client.get().uri("/all").accept(APPLICATION_JSON_UTF8).exchange()
				.flatMapMany(response -> response.bodyToFlux(Cursos.class));
	}

	@Override
	public Mono<Cursos> findCursosById(String id) {
		// TODO Auto-generated method stub
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("id", id);

		return client.get().uri("/id/{id}", params).accept(APPLICATION_JSON_UTF8).retrieve().bodyToMono(Cursos.class);
		// .exchange()
		// .flatMap(response -> response.bodyToMono(Cursos.class));
	}

	@Override
	public Mono<Cursos> saveCurso(Cursos document) {
		// TODO Auto-generated method stub
		return client.post().uri("/create-cursos").accept(APPLICATION_JSON_UTF8).contentType(APPLICATION_JSON_UTF8)
				.body(fromObject(document))
				// .syncBody(document)
				.retrieve().bodyToMono(Cursos.class);
	}

	@Override
	public Mono<Cursos> updateCurso(Cursos document, String id) {
		// TODO Auto-generated method stub

		return client.put().uri("/update-cursos/{id}", Collections.singletonMap("id", id)).accept(APPLICATION_JSON_UTF8)
				.contentType(APPLICATION_JSON_UTF8)
				// .body(fromObject(document))
				.syncBody(document).retrieve().bodyToMono(Cursos.class);
	}

	@Override
	public Mono<Void> deleteCurso(String id) {
		// TODO Auto-generated method stub
		return client.delete().uri("/delete-cursos-asset/{id}", Collections.singletonMap("id", id)).exchange().then();
	}

	@Override
	public Mono<Alumno> savesAlumnoCurso(Alumno document, String cursoId) {
		// TODO Auto-generated method stub
		List<String> cursosIds = Arrays.asList(cursoId);

		Flux<Cursos> cursos = Flux.fromIterable(cursosIds)

				.flatMap(x -> {
					System.out.println("Cursos seleccionados:  " + getCursosById(cursoId));
					return getCursosById(cursoId);
				});

		List<Cursos> cursox = new ArrayList<>();

		cursos.collectList().subscribe(cursox::addAll);
		document.setCursos(cursox);
		// TODO Auto-generated method stub
		return alumnoRepository.save(document);
	}

}
