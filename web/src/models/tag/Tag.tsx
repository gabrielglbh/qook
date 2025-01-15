import { StringFormatters } from "../../components/StringFormatters";
import TagDto from "./TagDto";

interface Tag {
  toDto(): TagDto;
}

class Tag {
  id: string;
  text: string;
  color: string;

  constructor(
    id: string,
    text: string,
    color: string,
  ) {
    this.id = id;
    this.text = text;
    this.color = color;
  }
}

Tag.prototype.toDto = function() {
  const keywords: string[] = [];
  keywords.push(...this.text.split(' ').map(e => e.toLowerCase()));
  keywords.push(...StringFormatters.generateSubStrings(this.text)); 
    
  return new TagDto(
    this.id,
    this.text,
    keywords,
    -1, // TODO: toArgb()
  );
}

export default Tag;
