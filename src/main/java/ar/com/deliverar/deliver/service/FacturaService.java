package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.dto.RegistrarFacturaDTO;
import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.ItemFactura;
import ar.com.deliverar.deliver.model.Proveedor;
import ar.com.deliverar.deliver.model.Factura.EstadoFactura;
import ar.com.deliverar.deliver.repository.FacturaRepository;
import ar.com.deliverar.deliver.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    // Crear una nueva factura para un proveedor específico
    public Factura emitirFactura(Long proveedorId, String numero, Double montoTotal) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + proveedorId));

        Factura factura = new Factura();
        factura.setProveedor(proveedor);
        factura.setNumero(numero);
        factura.setMontoTotal(montoTotal);
        factura.setFechaEmision(LocalDate.now());
        factura.setEstado(EstadoFactura.EMITIDA);

        return facturaRepository.save(factura);
    }

    // Cambiar el estado de una factura
    public Factura actualizarEstado(Long idFactura, EstadoFactura nuevoEstado) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + idFactura));

        factura.setEstado(nuevoEstado);
        return facturaRepository.save(factura);
    }

    // Obtener todas las facturas
    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    // Obtener una factura por ID
    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public Factura crearFacturaDesdeDto(RegistrarFacturaDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        Factura factura = new Factura();
        factura.setNumero(dto.getNumero() != null ? dto.getNumero() : UUID.randomUUID().toString());
        factura.setDescripcion(dto.getDescripcion());
        factura.setFechaEmision(dto.getFechaEmision() != null ? dto.getFechaEmision() : LocalDate.now());
        factura.setMontoTotal(dto.getMontoTotal());
        factura.setProveedor(proveedor);
        factura.setTipoComprobante(dto.getTipoComprobante());
        factura.setCondicionIva(dto.getCondicionIva());
        factura.setCuitEmisor(dto.getCuitEmisor());
        factura.setCuitReceptor(dto.getCuitReceptor());
        factura.setDomicilioFiscal(dto.getDomicilioFiscal());
        factura.setNotas(dto.getNotas());
        factura.setEstado(Factura.EstadoFactura.EMITIDA);
        factura.setCondicionPago("Contado");

        // Crear ítem automático
        ItemFactura item = new ItemFactura();
        item.setDescripcion(dto.getDescripcion());
        item.setCantidad(1);
        item.setPrecioUnitario(dto.getMontoTotal());
        item.setPorcentajeIVA(21.0);

        double subtotal = dto.getMontoTotal();
        double iva = subtotal * 0.21;
        double total = subtotal + iva;

        item.setSubtotal(subtotal);
        item.setMontoIVA(iva);
        item.setTotalItem(total);
        item.setFactura(factura);

        factura.setItems(List.of(item));
        factura.setIvaTotal(iva);

        return facturaRepository.save(factura);
    }

    // Obtener facturas por proveedor
    public List<Factura> obtenerPorProveedor(Long proveedorId) {
        return facturaRepository.findByProveedorId(proveedorId);
    }

    public Factura guardarFactura(Factura factura) {
        return facturaRepository.save(factura);
    }


    public Factura marcarComoPagada(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + facturaId));

        factura.setEstado(Factura.EstadoFactura.PAGADA);
        return facturaRepository.save(factura);
    }

    public byte[] generarPdf(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, baos);
        doc.open();

        doc.add(new Paragraph("Factura Nº: " + factura.getNumero()));
        doc.add(new Paragraph("Fecha: " + factura.getFechaEmision()));
        doc.add(new Paragraph("Monto Total: $" + factura.getMontoTotal()));
        doc.add(new Paragraph("Estado: " + factura.getEstado()));
        doc.add(new Paragraph("Proveedor: " + factura.getProveedor().getNombre()));
        doc.add(new Paragraph("CUIT Receptor: " + factura.getCuitReceptor()));
        doc.add(new Paragraph("CUIT Emisor: " + factura.getCuitEmisor()));
        doc.add(new Paragraph("Tipo Comprobante: " + factura.getTipoComprobante()));
        doc.add(new Paragraph("Condición IVA: " + factura.getCondicionIva()));
        doc.add(new Paragraph("Domicilio Fiscal: " + factura.getDomicilioFiscal()));

        doc.close();
        return baos.toByteArray();
    }

}
