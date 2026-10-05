const api = {
    async request(path, options = {}) {
        const response = await fetch(path, {
            ...options,
            headers: { "Content-Type": "application/json", ...options.headers }
        });
        if (!response.ok) {
            let detail = `Error ${response.status}`;
            try {
                const body = await response.json();
                detail = body.error || body.message || detail;
            } catch {
                detail = response.statusText || detail;
            }
            throw new Error(detail);
        }
        return response.status === 204 ? null : response.json();
    }
};

const byId = (id) => document.getElementById(id);
const message = byId("mensaje");
let patients = [];
let diagnoses = [];

function showMessage(text, isError = false) {
    message.textContent = text;
    message.classList.toggle("error", isError);
    message.hidden = false;
    window.scrollTo({ top: 0, behavior: "smooth" });
}

function clearMessage() {
    message.hidden = true;
    message.textContent = "";
}

function option(value, text) {
    const element = document.createElement("option");
    element.value = value;
    element.textContent = text;
    return element;
}

function appendCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = value || "—";
    row.append(cell);
}

async function loadDepartments() {
    const departments = await api.request("/departamentos");
    for (const selectId of ["departamento", "departamentoNuevoMunicipio"]) {
        const select = byId(selectId);
        select.replaceChildren(option("", "Seleccione departamento"));
        departments.forEach((department) => select.append(option(department.id, department.nombre)));
    }
}

async function loadMunicipalities(departmentId, selectedId = "") {
    const select = byId("municipioId");
    select.replaceChildren(option("", "Seleccione municipio"));
    select.disabled = !departmentId;
    if (!departmentId) return;
    const municipalities = await api.request(`/municipios?departamentoId=${encodeURIComponent(departmentId)}`);
    municipalities.forEach((municipality) => select.append(option(municipality.id, municipality.nombre)));
    select.value = selectedId;
}

async function loadDiagnoses() {
    diagnoses = await api.request("/diagnosticos");
    const select = byId("diagnosticoId");
    select.replaceChildren(option("", "Seleccione diagnóstico"));
    diagnoses.forEach((diagnosis) => select.append(option(diagnosis.codigo, `${diagnosis.codigo} · ${diagnosis.nombre}`)));
}

function renderPatients() {
    const body = byId("tabla-pacientes");
    const assignSelect = byId("pacienteDiagnostico");
    body.replaceChildren();
    assignSelect.replaceChildren(option("", "Seleccione paciente"));
    if (!patients.length) {
        body.append(emptyRow(6, "No hay pacientes registrados."));
        return;
    }
    patients.forEach((patient) => {
        const row = document.createElement("tr");
        appendCell(row, `${patient.nombre} ${patient.apellido}`);
        appendCell(row, patient.fechaNacimiento);
        appendCell(row, patient.municipio);
        appendCell(row, patient.departamento);
        appendCell(row, patient.nombreDiagnostico ? `${patient.codigoDiagnostico} · ${patient.nombreDiagnostico}` : "Sin diagnóstico");
        const actions = document.createElement("td");
        actions.className = "actions";
        const edit = document.createElement("button");
        edit.type = "button";
        edit.className = "secondary";
        edit.textContent = "Editar";
        edit.addEventListener("click", () => editPatient(patient));
        const remove = document.createElement("button");
        remove.type = "button";
        remove.className = "danger";
        remove.textContent = "Eliminar";
        remove.addEventListener("click", () => deletePatient(patient));
        actions.append(edit, remove);
        row.append(actions);
        body.append(row);
        assignSelect.append(option(patient.id, `${patient.nombre} ${patient.apellido}`));
    });
}

function emptyRow(columns, text) {
    const row = document.createElement("tr");
    const cell = document.createElement("td");
    cell.colSpan = columns;
    cell.className = "empty";
    cell.textContent = text;
    row.append(cell);
    return row;
}

async function loadDiagnosed() {
    const diagnosed = await api.request("/pacientes/diagnosticados");
    const body = byId("tabla-diagnosticados");
    body.replaceChildren();
    if (!diagnosed.length) {
        body.append(emptyRow(6, "Aún no hay pacientes con diagnóstico asignado."));
        return;
    }
    diagnosed.forEach((patient) => {
        const row = document.createElement("tr");
        appendCell(row, patient.codigoDiagnostico);
        appendCell(row, patient.nombreDiagnostico);
        appendCell(row, patient.observacionMedica);
        appendCell(row, `${patient.nombre} ${patient.apellido}`);
        appendCell(row, patient.municipio);
        appendCell(row, patient.departamento);
        body.append(row);
    });
}

async function refresh() {
    [patients, diagnoses] = await Promise.all([
        api.request("/pacientes"),
        api.request("/diagnosticos")
    ]);
    renderPatients();
    const diagnosisSelect = byId("diagnosticoId");
    diagnosisSelect.replaceChildren(option("", "Seleccione diagnóstico"));
    diagnoses.forEach((diagnosis) => diagnosisSelect.append(
        option(diagnosis.codigo, `${diagnosis.codigo} · ${diagnosis.nombre}`)
    ));
    await loadDiagnosed();
}

function resetPatientForm() {
    byId("form-paciente").reset();
    byId("paciente-id").value = "";
    byId("municipioId").replaceChildren(option("", "Seleccione municipio"));
    byId("municipioId").disabled = true;
    byId("modo-paciente").textContent = "Nuevo paciente";
    byId("guardar-paciente").textContent = "Guardar paciente";
    byId("cancelar-edicion").hidden = true;
}

async function editPatient(patient) {
    byId("paciente-id").value = patient.id;
    byId("nombre").value = patient.nombre;
    byId("apellido").value = patient.apellido;
    byId("fechaNacimiento").value = patient.fechaNacimiento;
    byId("departamento").value = patient.departamentoId;
    await loadMunicipalities(patient.departamentoId, patient.municipioId);
    byId("modo-paciente").textContent = "Editando paciente";
    byId("guardar-paciente").textContent = "Guardar cambios";
    byId("cancelar-edicion").hidden = false;
    byId("nombre").focus();
}

async function deletePatient(patient) {
    if (!window.confirm(`¿Eliminar a ${patient.nombre} ${patient.apellido}?`)) return;
    try {
        await api.request(`/pacientes/${patient.id}`, { method: "DELETE" });
        await refresh();
        showMessage("Paciente eliminado correctamente.");
    } catch (error) {
        showMessage(error.message, true);
    }
}

byId("departamento").addEventListener("change", async (event) => {
    try {
        await loadMunicipalities(event.target.value);
    } catch (error) {
        showMessage(error.message, true);
    }
});

byId("form-paciente").addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessage();
    const id = byId("paciente-id").value;
    const data = {
        nombre: byId("nombre").value.trim(),
        apellido: byId("apellido").value.trim(),
        fechaNacimiento: byId("fechaNacimiento").value,
        municipioId: Number(byId("municipioId").value)
    };
    try {
        await api.request(id ? `/pacientes/${id}` : "/pacientes", {
            method: id ? "PUT" : "POST",
            body: JSON.stringify(data)
        });
        resetPatientForm();
        await refresh();
        showMessage(id ? "Paciente actualizado correctamente." : "Paciente creado correctamente.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

byId("cancelar-edicion").addEventListener("click", resetPatientForm);
byId("recargar").addEventListener("click", async () => {
    try {
        await refresh();
        showMessage("Listas actualizadas.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

byId("form-diagnostico").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        await api.request(`/pacientes/${byId("pacienteDiagnostico").value}/diagnostico`, {
            method: "POST",
            body: JSON.stringify({
                codigoDiagnostico: byId("diagnosticoId").value,
                observacion: byId("observacion").value.trim()
            })
        });
        byId("form-diagnostico").reset();
        await refresh();
        showMessage("Diagnóstico y observación asignados correctamente.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

byId("form-departamento").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        await api.request("/departamentos", {
            method: "POST",
            body: JSON.stringify({ nombre: byId("nuevo-departamento").value.trim() })
        });
        byId("form-departamento").reset();
        await loadDepartments();
        showMessage("Departamento agregado al catálogo.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

byId("form-municipio").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        await api.request("/municipios", {
            method: "POST",
            body: JSON.stringify({
                nombre: byId("nuevo-municipio").value.trim(),
                departamentoId: Number(byId("departamentoNuevoMunicipio").value)
            })
        });
        byId("form-municipio").reset();
        await loadMunicipalities(byId("departamento").value);
        showMessage("Municipio agregado al departamento seleccionado.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

byId("form-nuevo-diagnostico").addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
        await api.request("/diagnosticos", {
            method: "POST",
            body: JSON.stringify({
                codigo: byId("codigo-diagnostico").value.trim(),
                nombre: byId("nombre-diagnostico").value.trim(),
                descripcion: byId("descripcion-diagnostico").value.trim()
            })
        });
        byId("form-nuevo-diagnostico").reset();
        await refresh();
        showMessage("Diagnóstico agregado al catálogo.");
    } catch (error) {
        showMessage(error.message, true);
    }
});

async function start() {
    try {
        await loadDepartments();
        await refresh();
    } catch (error) {
        showMessage(`No fue posible cargar los catálogos. ${error.message}`, true);
    }
}

start();
