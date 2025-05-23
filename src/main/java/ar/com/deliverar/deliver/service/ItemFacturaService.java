package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.dto.ItemFacturaDTO;
import ar.com.deliverar.deliver.model.Factura;
import ar.com.deliverar.deliver.model.ItemFactura;
import ar.com.deliverar.deliver.repository.FacturaRepository;
import ar.com.deliverar.deliver.repository.ItemFacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemFacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ItemFacturaRepository itemFacturaRepository;

    public ItemFactura agregarItem(Long facturaId, ItemFacturaDTO dto) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + facturaId));

        ItemFactura item = new ItemFactura();
        item.setFactura(factura);
        item.setDescripcion(dto.getDescripcion());
        item.setCantidad(dto.getCantidad());
        item.setPrecioUnitario(dto.getPrecioUnitario());
        item.setPorcentajeIVA(dto.getPorcentajeIVA());

        double subtotal = dto.getCantidad() * dto.getPrecioUnitario();
        double iva = subtotal * dto.getPorcentajeIVA() / 100;
        double total = subtotal + iva;

        item.setSubtotal(subtotal);
        item.setMontoIVA(iva);
        item.setTotalItem(total);

        itemFacturaRepository.save(item);

        recalcularFactura(factura);
        return item;
    }

    public ItemFactura actualizarItem(Long facturaId, Long itemId, ItemFacturaDTO dto) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        ItemFactura item = itemFacturaRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        item.setDescripcion(dto.getDescripcion());
        item.setCantidad(dto.getCantidad());
        item.setPrecioUnitario(dto.getPrecioUnitario());
        item.setPorcentajeIVA(dto.getPorcentajeIVA());

        double subtotal = dto.getCantidad() * dto.getPrecioUnitario();
        double iva = subtotal * dto.getPorcentajeIVA() / 100;
        double total = subtotal + iva;

        item.setSubtotal(subtotal);
        item.setMontoIVA(iva);
        item.setTotalItem(total);

        itemFacturaRepository.save(item);
        recalcularFactura(factura);

        return item;
    }

    public List<ItemFactura> obtenerItemsPorFactura(Long facturaId) {
        return itemFacturaRepository.findByFacturaId(facturaId);
    }

    private void recalcularFactura(Factura factura) {
        List<ItemFactura> items = itemFacturaRepository.findByFacturaId(factura.getId());

        double montoTotal = items.stream().mapToDouble(ItemFactura::getTotalItem).sum();
        double ivaTotal = items.stream().mapToDouble(ItemFactura::getMontoIVA).sum();

        factura.setMontoTotal(montoTotal);
        factura.setIvaTotal(ivaTotal);
        facturaRepository.save(factura);
    }
}
