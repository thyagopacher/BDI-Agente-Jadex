/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import br.com.agent.belief.QualityAttributeClass;

/**
 *
 * @author ThyagoHenrique
 */
public class TableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* Lista de Sócios que representam as linhas. */
	private List<QualityAttributeClass> rows = new ArrayList<>(100);

	/* Array de Strings com o nome das colunas. */
	private String[] columns = new String[] { "Class", "File", "Reusability", "Maintenance", "Reability"};

	public TableModel() {
		rows = new ArrayList<>(100);
	}

	public TableModel(List<QualityAttributeClass> rows2) {
		rows = new ArrayList<>(rows2);
	}

	/* Retorna a quantidade de colunas. */
	@Override
	public int getColumnCount() {
		return columns.length;
	}

	/* Retorna a quantidade de linhas. */
	@Override
	public int getRowCount() {
		return rows.size();
	}

	/*
	 * Retorna o nome da coluna no índice especificado. Este método é usado pela
	 * JTable para saber o texto do cabeçalho.
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return columns[columnIndex];
	};

	/*
	 * Retorna a classe dos elementos da coluna especificada. Este método é usado
	 * pela JTable na hora de definir o editor da célula.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 1:
			return String.class;			
		case 2:
			return int.class;			
		case 3:
			return int.class;
		case 4:
			return int.class;
		default:
			throw new IndexOutOfBoundsException("columnIndex out of bounds");
		}
	}

	/*
	 * Retorna o valor da célula especificada pelos índices da linha e da coluna.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// Pega o sócio da linha especificada.
		QualityAttributeClass objeto = rows.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return objeto.getNameClass();
		case 1:
			return objeto.getFileClass();			
		case 2:
			return objeto.getImpromentReusability();
		case 3:{
			return objeto.getImpromentMaintenance();
		}
		case 4:
			return objeto.getImpromentReability();
		default:
			throw new IndexOutOfBoundsException("columnIndex out of bounds");
		}
	}

	/*
	 * Seta o valor da célula especificada pelos índices da linha e da coluna. Aqui
	 * ele está implementado para não fazer nada, até porque este table model não é
	 * editável.
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int col) {
		fireTableRowsUpdated(rowIndex, rowIndex);
	}

	public QualityAttributeClass getObjeto(int indiceLinha) {
		return rows.get(indiceLinha);
	}

	/* Adiciona um registro. */
	public void addObjeto(QualityAttributeClass Objeto) {
		rows.add(Objeto);
		int ultimoIndice = getRowCount() - 1;
		fireTableRowsInserted(ultimoIndice, ultimoIndice);
	}

	/* Remove a linha especificada. */
	public void removeObjeto(int indiceLinha) {
		rows.remove(indiceLinha);
		fireTableRowsDeleted(indiceLinha, indiceLinha);
	}

	/* Adiciona uma lista de sócios ao final dos registros. */
	public void addListaDeObjetos(List<QualityAttributeClass> Objetos) {
		int tamanhoAntigo = getRowCount();
		rows.addAll(Objetos);
		fireTableRowsInserted(tamanhoAntigo, getRowCount() - 1);
	}

	/* Remove todos os registros. */
	public void limpar() {
		rows.clear();
		fireTableDataChanged();
	}

	/* Verifica se este table model está vazio. */
	public boolean isEmpty() {
		return rows.isEmpty();
	}
}
