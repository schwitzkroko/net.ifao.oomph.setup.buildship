package schemagenerator.correctors;

import net.ifao.xml.XmlObject;

/** 
 * Applies schema corrections for Best Western OTA schemas. This class 
 * uses AbstractXmlObjectCorrector classes to do it's job. 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author wunder 
 */
public final class BestWesternCorrector extends AbstractCorrector {

	/** 
	 * Abstract class for applying corrections to a certain xml element. 
	 * 
	 * <p> 
	 * Copyright &copy; 2010, i:FAO 
	 * 
	 * @author wunder 
	 */
	private static abstract class AbstractXmlObjectCorrector {
		/**
		 * Apply the correction, if applicable
		 * @param pXml The element to correct.
		 * @return true, if a corrections is applicable (this class
		 * is responsible for that element) or false, if not. It this method
		 * returns true, no other corrector is called for this element anymore.
		 */
		protected abstract boolean correct(XmlObject pXml);

		/**
		 * Overwrite this method and return a description what this correction does and why it is applied.
		 * @return The description text.
		 */
		public abstract String getCorrectionSummary();
	}

	/** 
	 * This array contains all certain element Best Western correctors. 
	 * 
	 * @author wunder 
	 */
	private final AbstractXmlObjectCorrector[] _correctors = {
			new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name").equals("RatePlanType")) {
							XmlObject sequence = pXml.getObject("sequence");
							sequence.setName(sequence.getNameSpace(), "choice");
							sequence.setAttribute("maxOccurs", "unbounded");
							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Replacing the 'RatePlanType' sequence by a choice because the response doesn't match the order of the subelements.";
				}
			}, new AbstractXmlObjectCorrector() {
				@Override
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name").equals("EmailType")) {

							pXml.deleteObjects("simpleContent");

							XmlObject emailSequence = pXml
									.createObject("xs:sequence");
							XmlObject text = new XmlObject("<xs:element />")
									.getFirstObject();
							text.setAttribute("name", "Text");
							text.setAttribute("type", "xs:string");
							emailSequence.addObject(text);

							XmlObject attribs = new XmlObject(
									"<xs:attribute />").getFirstObject();
							attribs.setAttribute("name", "EmailType");
							attribs.setAttribute("type", "OTA_CodeType");
							attribs.setAttribute("use", "optional");
							pXml.addObject(attribs);

							XmlObject anno = new XmlObject("<xs:annotation />")
									.getFirstObject();
							attribs.addObject(anno);
							XmlObject docu = new XmlObject(
									"<xs:documentation>Defines the purpose of the e-mail address (e.g. personal, business, listserve). Refer to OTA Code List Email Address Type (EAT).</xs:documentation>")
									.getFirstObject();
							anno.addObject(docu);
							docu.setAttribute("xml:lang", "en");

							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Changing 'EmailType' by adding a 'Text' sub-element and modifying the 'EmailType' attributes, because the response uses a 'Text' sub-element that doesn't exist in the original schema.";
				}
			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name").equals("PaymentCardType")) {
							XmlObject[] attributes = pXml
									.getObjects("attribute");
							for (int i = 0; i < attributes.length; i++) {
								if (attributes[i].getAttribute("name").equals(
										"CardNumber")) {
									attributes[i].setAttribute("type",
											"xs:string");
									return true;
								}
							}
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Changing the 'PaymentCardType' 'CardNumber' attribute type to string because the response contains masking characters ('*')";
				}
			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("simpleType")) {
						if (pXml.getAttribute("name").equals("DurationType")) {
							XmlObject union = pXml.getObject("union");
							union.setName(union.getNameSpace(), "restriction");
							union.setAttribute("base", "xs:string");
							union.setAttribute("memberTypes", null);
							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Changing the DurationType type to string because the response doesn't match the original schema type.";
				}
			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name").equals("CustomerType")) {
							XmlObject sequence = pXml.getObject("sequence");
							sequence.setName(sequence.getNameSpace(), "choice");
							sequence.setAttribute("maxOccurs", "unbounded");

							XmlObject addresses = new XmlObject(
									"<xs:element />").getFirstObject();
							sequence.addObject(addresses);
							addresses.setAttribute("name", "Addresses");
							addresses.setAttribute("minOccurs", "0");

							XmlObject addressComplexType = addresses
									.createObject("xs:complexType");
							XmlObject addressSequence = addressComplexType
									.createObject("xs:sequence");

							XmlObject[] xmlObjects = sequence
									.getObjects("element");
							for (int i = 0; i < xmlObjects.length; i++) {
								String elementNameAttribute = xmlObjects[i]
										.getAttribute("name");
								if (elementNameAttribute.equals("Address")) {
									addressSequence.addObject(xmlObjects[i]);
								}
							}
							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Embedding the 'Address' element into a 'Addresses' element because the response does it so. But the request doesn't use the 'Addresses' element, so the 'Address' element is also left.";
				}
			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("attributeGroup")) {
						if (pXml.getAttribute("name").equals(
								"TelephoneAttributesGroup")) {

							XmlObject[] attributes = pXml
									.getObjects("attribute");

							for (int i = 0; i < attributes.length; i++) {
								if (attributes[i].getAttribute("name").equals(
										"PhoneNumber")) {
									attributes[i].setAttribute("type",
											"xs:string");
								}
							}

							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Changing the 'PhoneNumber' type to string because it may be empty.";
				}

			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name")
								.equals("DateTimeSpanType")) {

							XmlObject[] choice = pXml.getObjects("choice");

							for (int i = 0; i < choice.length; i++) {
								choice[i].setAttribute("minOccurs", "0");
							}

							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Changing the minOccurs of the embedding choice of element DateTimeSpanType from 1 to 0 because Castor has a problem to unmarshall and empty choice.";
				}

			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name").equals("PersonNameType")) {

							XmlObject[] sequence = pXml.getObjects("sequence");

							for (int iSequence = 0; iSequence < sequence.length; iSequence++) {

								XmlObject[] element = sequence[iSequence]
										.getObjects("element");

								for (int iElement = 0; iElement < element.length; iElement++) {
									if (element[iElement].getAttribute("name")
											.equals("GivenName")) {
										element[iElement].setAttribute("type",
												"xs:string");
									}
								}
							}
							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Changing the type of GivenName to string because in the response, it may be empty.";
				}

			}, new AbstractXmlObjectCorrector() {
				/**
				 * See return value of getCorrectionSummay Method.
				 */
				@Override
				public boolean correct(XmlObject pXml) {
					if (pXml.getName().equals("complexType")) {
						if (pXml.getAttribute("name").equals("TPA_Extensions_Type")) {
							pXml.deleteObjects("annotation");
							pXml.deleteObjects("sequence");
							pXml.addObject(new XmlObject(
							"<xs:annotation><xs:documentation xml:lang=\"en\">Allows extensions to be added to the OTA specification per trading partner agreement.</xs:documentation></xs:annotation>").getFirstObject());							
							pXml.addObject(new XmlObject(
							"<xs:choice><xs:element name=\"BookingCode\" type=\"xs:string\"/><xs:element name=\"ExchangeRate\"><xs:complexType><xs:attribute name=\"Percent\" type=\"xs:string\" use=\"required\"/></xs:complexType></xs:element><xs:element name=\"LimitedDistributionTradingPartners\"><xs:complexType><xs:sequence><xs:element name=\"TradingPartner\" maxOccurs=\"unbounded\"><xs:complexType><xs:attribute name=\"Code\" type=\"xs:string\" use=\"optional\"/><xs:attribute name=\"Description\" type=\"xs:string\" use=\"optional\"></xs:attribute></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:choice>").getFirstObject());							
							return true;
						}
					}
					return false;
				}

				/**
				 * Returns the correction summary.
				 */
				@Override
				public String getCorrectionSummary() {
					return "Adding element 'BookingCode' to element 'TPA_Extensions' because this is required by BW but not defined in the schema!";
				}

			}

	};

	/** 
	 * For each visited Xml element, calls the certain element's corrector object. Iterates through all 
	 * correctors until one of the returns true. 
	 * 
	 * @param pXml The currecntly visited xml element. 
	 * 
	 * @author wunder 
	 */
	@Override
	protected void correctXmlObject(XmlObject pXml) {

		for (int i = 0; i < _correctors.length; i++) {
			if (_correctors[i].correct(pXml)) {
				return;
			}
		}
	}

	/** 
	 * Returns the correction summary for Best Western. 
	 * 
	 * @return The correction summary. 
	 * 
	 * @author wunder 
	 */
	@Override
	public String getCorrectionSummary() {
		StringBuilder sbCorrectionSummary = new StringBuilder();
		for (int i = 0; i < _correctors.length; i++) {
			sbCorrectionSummary.append("- ").append(
					_correctors[i].getCorrectionSummary()).append("\n");
		}
		return sbCorrectionSummary.toString();
	}

}
