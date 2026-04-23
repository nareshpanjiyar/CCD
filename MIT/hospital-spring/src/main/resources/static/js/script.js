/**
 * Hospital Management System - Custom JavaScript
 * Compatible with Bootstrap 5 and Thymeleaf
 */

document.addEventListener('DOMContentLoaded', function() {

    // ---------- Sidebar Toggle ----------
    const sidebarCollapseBtn = document.getElementById('sidebarCollapse');
    const sidebar = document.getElementById('sidebar');
    if (sidebarCollapseBtn && sidebar) {
        sidebarCollapseBtn.addEventListener('click', function() {
            sidebar.classList.toggle('active');
        });
    }

    // ---------- Auto-hide Alerts after 3 seconds ----------
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 3000);
    });

    // ---------- Generic Table Search (use class 'searchable-table' on table, and id 'searchInput' on input) ----------
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        const table = document.querySelector('.searchable-table');
        if (table) {
            const tbody = table.querySelector('tbody');
            searchInput.addEventListener('keyup', function() {
                const filter = this.value.toUpperCase();
                const rows = tbody.querySelectorAll('tr');
                rows.forEach(row => {
                    // Skip rows with no data or colspan messages
                    if (row.cells.length < 2) return;
                    const text = row.textContent.toUpperCase();
                    row.style.display = text.includes(filter) ? '' : 'none';
                });
            });
        }
    }

    // ---------- Dynamic Medication Rows (Prescription Form) ----------
    const addMedicationBtn = document.getElementById('addMedicationBtn');
    const medicationList = document.getElementById('medication-list');
    if (addMedicationBtn && medicationList) {
        addMedicationBtn.addEventListener('click', function() {
            addMedicationRow();
        });
    }

    // ---------- Remove Medication Row ----------
    document.addEventListener('click', function(e) {
        if (e.target.closest('.remove-medication-btn')) {
            const row = e.target.closest('.medication-item');
            if (row) {
                const container = document.getElementById('medication-list');
                // Don't remove if it's the last row
                if (container && container.children.length > 1) {
                    row.remove();
                } else {
                    alert('At least one medication is required.');
                }
            }
        }
    });

});

/**
 * Adds a new medication row to the prescription form.
 * Call this function from inline onclick as well.
 */
function addMedicationRow() {
    const container = document.getElementById('medication-list');
    if (!container) return;

    // Clone the first medication item as template
    const firstItem = container.querySelector('.medication-item');
    if (!firstItem) return;

    const newRow = firstItem.cloneNode(true);

    // Clear input values
    newRow.querySelectorAll('input, select, textarea').forEach(field => {
        if (field.tagName === 'SELECT') {
            field.selectedIndex = 0;
        } else {
            field.value = '';
        }
    });

    // Update name attributes to maintain array indexing (handled by Thymeleaf automatically if using fields)
    // No need to change names if using Thymeleaf's dynamic field binding

    // Add remove button if not present
    if (!newRow.querySelector('.remove-medication-btn')) {
        const colDiv = document.createElement('div');
        colDiv.className = 'col-md-1 d-flex align-items-end';
        colDiv.innerHTML = '<button type="button" class="btn btn-outline-danger w-100 remove-medication-btn"><i class="fas fa-times"></i></button>';
        newRow.appendChild(colDiv);
    }

    container.appendChild(newRow);
}

/**
 * Reset modal form to default state (for Add New operations).
 * @param {string} modalId - The ID of the modal element.
 * @param {string} formId - The ID of the form inside the modal.
 */
function resetModalForm(modalId, formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.reset();
        // Clear hidden ID field
        const idField = form.querySelector('input[name$=".id"], input[name="id"]');
        if (idField) idField.value = '';
    }
    // Update modal title
    const modalTitle = document.querySelector(`#${modalId} .modal-title`);
    if (modalTitle) {
        modalTitle.innerHTML = modalTitle.innerHTML.replace('Edit', 'Add New').replace('Update', 'Add New');
    }
}

/**
 * Populate modal form for editing.
 * @param {object} data - Key-value pairs of field names and values.
 * @param {string} formId - The ID of the form.
 */
function populateEditForm(data, formId) {
    const form = document.getElementById(formId);
    if (!form) return;
    for (const [key, value] of Object.entries(data)) {
        const field = form.querySelector(`[name="${key}"]`);
        if (field) {
            field.value = value || '';
        }
    }
}

/**
 * Confirm delete action with custom message.
 * @param {string} message - The confirmation message.
 * @returns {boolean} - True if confirmed.
 */
function confirmDelete(message = 'Are you sure you want to delete this item?') {
    return confirm(message);
}

/**
 * Filter table rows based on multiple criteria (used in admin tables).
 * @param {string} tableId - The ID of the table.
 * @param {object} filters - Key-value pairs of data attributes to filter.
 */
function filterTable(tableId, filters = {}) {
    const table = document.getElementById(tableId);
    if (!table) return;
    const tbody = table.querySelector('tbody');
    if (!tbody) return;
    const rows = tbody.querySelectorAll('tr');
    const searchTerm = filters.search ? filters.search.toLowerCase() : '';
    const statusFilter = filters.status || 'all';
    const dateFilter = filters.date || '';

    rows.forEach(row => {
        if (row.cells.length < 2) return;
        let show = true;
        const text = row.textContent.toLowerCase();
        if (searchTerm && !text.includes(searchTerm)) show = false;
        if (statusFilter !== 'all' && row.dataset.status !== statusFilter) show = false;
        if (dateFilter && row.dataset.date !== dateFilter) show = false;
        row.style.display = show ? '' : 'none';
    });
}

/**
 * Format date for display.
 * @param {string|Date} date - The date to format.
 * @returns {string} - Formatted date (e.g., "Jan 01, 2025").
 */
function formatDate(date) {
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' });
}

/**
 * Format date and time for display.
 * @param {string|Date} date - The date to format.
 * @returns {string} - Formatted date and time (e.g., "Jan 01, 2025 10:30 AM").
 */
function formatDateTime(date) {
    const d = new Date(date);
    return d.toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }) + 
           ' ' + d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });
}